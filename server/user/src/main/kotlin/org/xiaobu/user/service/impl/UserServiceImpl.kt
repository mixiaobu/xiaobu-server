package org.xiaobu.user.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.xiaobu.core.constant.RedisConstants.EMAIL_LOGIN_CAPTCHA_PREFIX
import org.xiaobu.core.constant.RedisConstants.EMAIL_UPDATE_CAPTCHA_PREFIX
import org.xiaobu.core.entity.Response
import org.xiaobu.core.util.RegexUtil
import org.xiaobu.core.util.isNotNullOrEmpty
import org.xiaobu.core.util.isNullOrEmpty
import org.xiaobu.redis.service.RedisService
import org.xiaobu.user.entity.Authority
import org.xiaobu.user.entity.Role
import org.xiaobu.user.entity.User
import org.xiaobu.user.mapper.UserMapper
import org.xiaobu.user.service.*

@Service
class UserServiceImpl(
    val roleService: RoleService,
    val userRoleService: UserRoleService,
    val authorityService: AuthorityService,
    val roleAuthorityService: RoleAuthorityService,
    val redisService: RedisService,
    val passwordEncoder: PasswordEncoder,
) : ServiceImpl<UserMapper, User>(), UserService {

    override fun findById(id: String): User? {
        return super<ServiceImpl>.getById(id)?.apply {
            password = ""
        }
    }

    override fun findByUsername(username: String): User? {
        return ktQuery().eq(User::username, username).one()?.apply {
            password = ""
        }
    }

    override fun findByNickname(nickname: String): User? {
        return ktQuery().eq(User::nickname, nickname).one()?.apply {
            password = ""
        }
    }

    override fun findByEmail(email: String): User? {
        return ktQuery().eq(User::email, email).one()?.apply {
            password = ""
        }
    }

    override fun findInIds(ids: MutableList<String>): MutableList<User> {
        if (ids.isNotNullOrEmpty()) {
            return ktQuery().`in`(User::id, ids).list().onEach {
                it.password = ""
            }
        } else {
            return mutableListOf()
        }
    }

    override fun findByIdWithPassword(id: String): User? {
        return super<ServiceImpl>.getById(id)
    }

    override fun findByUsernameWithPassword(username: String): User? {
        return ktQuery().eq(User::username, username).one()
    }

    override fun findByEmailWithPassword(email: String): User? {
        return ktQuery().eq(User::email, email).one()
    }

    override fun findRoleByUsername(username: String): MutableList<Role> {
        val user = findByUsernameWithPassword(username) ?: return mutableListOf()
        val roleIds = userRoleService.findByUserId(user.id).map { it.roleId }.toMutableList()
        val roles = roleService.findInIds(roleIds)
        return roles
    }

    override fun findAuthorityByUsername(username: String): MutableList<Authority> {
        val user = findByUsernameWithPassword(username) ?: return mutableListOf()
        val roleIds = userRoleService.findByUserId(user.id).map { it.roleId }.toMutableList()
        val authorityIds = roleAuthorityService.findInRoleIds(roleIds).map { it.authorityId }.toMutableList()
        val authorities = authorityService.findInIds(authorityIds)
        return authorities
    }

    override fun findAuthorityById(id: String): MutableList<Authority> {
        val user = findByIdWithPassword(id) ?: return mutableListOf()
        val roleIds = userRoleService.findByUserId(user.id).map { it.roleId }.toMutableList()
        val authorityIds = roleAuthorityService.findInRoleIds(roleIds).map { it.authorityId }.toMutableList()
        val authorities = authorityService.findInIds(authorityIds)
        return authorities
    }

    override fun getById(id: String): Response {
        val user = findById(id)
        if (user.isNotNullOrEmpty()) {
            return Response.success("获取成功", user)
        } else {
            return Response.error("用户不存在")
        }
    }

    override fun getByUsername(username: String): Response {
        val user = findByUsername(username)
        if (user.isNotNullOrEmpty()) {
            return Response.success("获取成功", user)
        } else {
            return Response.error("用户不存在")
        }
    }

    override fun updateNickname(nickname: String): Response {
        RegexUtil.validNickname(nickname).let {
            if (!it.status) {
                return Response.error(it.message)
            }
        }
        if (findByNickname(nickname).isNotNullOrEmpty()) {
            return Response.error("昵称已存在")
        }
        val userId = (SecurityContextHolder.getContext().authentication?.principal as Jwt).getClaim<String>("userId")
        val user = findByIdWithPassword(userId) ?: return Response.error("用户不存在")
        user.nickname = nickname
        return if (updateById(user)) {
            Response.success("修改成功", user.apply { this.password = "" })
        } else {
            Response.error("修改失败")
        }
    }

    override fun updateAvatarUrl(avatarUrl: String): Response {
        val userId = (SecurityContextHolder.getContext().authentication?.principal as Jwt).getClaim<String>("userId")
        val user = findByIdWithPassword(userId) ?: return Response.error("用户不存在")
        user.avatarUrl = avatarUrl
        return if (updateById(user)) {
            Response.success("修改成功", user.apply { this.password = "" })
        } else {
            Response.error("修改失败")
        }
    }

    override fun updateEmailByPassword(password: String, newEmail: String, newEmailCaptcha: String): Response {
        if (!RegexUtil.validEmail(newEmail)) {
            return Response.error("新邮箱格式不正确")
        }
        findByEmail(newEmail)?.let {
            return Response.error("新邮箱已存在")
        }
        val emailUpdateCaptcha = redisService.get(EMAIL_UPDATE_CAPTCHA_PREFIX + newEmail)?.let { it as String }
            ?: return Response.error("新邮箱验证码未发送或已过期")
        if (newEmailCaptcha != emailUpdateCaptcha) {
            return Response.error("新邮箱验证码错误")
        }
        val userId = (SecurityContextHolder.getContext().authentication?.principal as Jwt).getClaim<String>("userId")
        val user = findByIdWithPassword(userId) ?: return Response.error("用户不存在")
        if (passwordEncoder.matches(password, user.password).not()) {
            return Response.error("密码错误")
        }
        redisService.remove(EMAIL_UPDATE_CAPTCHA_PREFIX + newEmail)
        user.email = newEmail
        return if (updateById(user)) {
            Response.success("修改成功", user.apply { this.password = "" })
        } else {
            Response.error("修改失败")
        }
    }

    override fun updateEmailByCaptcha(
        email: String, emailCaptcha: String, newEmail: String, newEmailCaptcha: String
    ): Response {
        if (!RegexUtil.validEmail(email)) {
            return Response.error("旧邮箱格式不正确")
        }
        if (!RegexUtil.validEmail(newEmail)) {
            return Response.error("新邮箱格式不正确")
        }
        findByEmail(email) ?: run {
            return Response.error("旧邮箱不存在")
        }
        findByEmail(newEmail)?.let {
            return Response.error("新邮箱已存在")
        }
        val emailLoginCaptcha = redisService.get(EMAIL_LOGIN_CAPTCHA_PREFIX + email)?.let { it as String }
            ?: return Response.error("旧邮箱验证码未发送或已过期")
        val emailUpdateCaptcha = redisService.get(EMAIL_UPDATE_CAPTCHA_PREFIX + newEmail)?.let { it as String }
            ?: return Response.error("新邮箱验证码未发送或已过期")
        if (emailCaptcha != emailLoginCaptcha) {
            return Response.error("旧邮箱验证码错误")
        }
        if (newEmailCaptcha != emailUpdateCaptcha) {
            return Response.error("新邮箱验证码错误")
        }
        val userId = (SecurityContextHolder.getContext().authentication?.principal as Jwt).getClaim<String>("userId")
        val user = findByIdWithPassword(userId) ?: return Response.error("用户不存在")
        if (user.email != email) {
            return Response.error("旧邮箱错误")
        }
        redisService.remove(EMAIL_LOGIN_CAPTCHA_PREFIX + email)
        redisService.remove(EMAIL_UPDATE_CAPTCHA_PREFIX + newEmail)
        user.email = newEmail
        return if (updateById(user)) {
            Response.success("修改成功", user.apply { this.password = "" })
        } else {
            Response.error("修改失败")
        }
    }

    override fun updateGender(gender: String): Response {
        val genders = mutableListOf("保密", "男", "女")
        if (genders.contains(gender).not()) {
            return Response.error("性别只能是保密、男、女")
        }
        val userId = (SecurityContextHolder.getContext().authentication?.principal as Jwt).getClaim<String>("userId")
        val user = findByIdWithPassword(userId) ?: return Response.error("用户不存在")
        user.gender = gender
        return if (updateById(user)) {
            Response.success("修改成功", user.apply { this.password = "" })
        } else {
            Response.error("修改失败")
        }
    }

    override fun updateSignature(signature: String): Response {
        if (signature.contains("\n")) {
            return Response.error("签名不能包含换行")
        }
        if (signature.isNullOrEmpty()) {
            return Response.error("签名不能为空")
        }
        if (signature.length > 50) {
            return Response.error("签名最多50个字符")
        }
        val userId = (SecurityContextHolder.getContext().authentication?.principal as Jwt).getClaim<String>("userId")
        val user = findByIdWithPassword(userId) ?: return Response.error("用户不存在")
        user.signature = signature
        return if (updateById(user)) {
            Response.success("修改成功", user.apply { this.password = "" })
        } else {
            Response.error("修改失败")
        }
    }

    override fun updatePasswordByPassword(password: String, newPassword: String): Response {
        RegexUtil.validPassword(newPassword).let {
            if (!it.status) {
                return Response.error(it.message)
            }
        }
        val userId = (SecurityContextHolder.getContext().authentication?.principal as Jwt).getClaim<String>("userId")
        val user = findByIdWithPassword(userId) ?: return Response.error("用户不存在")
        if (passwordEncoder.matches(password, user.password).not()) {
            return Response.error("旧密码错误")
        }
        user.password = passwordEncoder.encode(newPassword).toString()
        return if (updateById(user)) {
            Response.success("修改成功", user.apply { this.password = "" })
        } else {
            Response.error("修改失败")
        }
    }

    override fun updatePasswordByCaptcha(email: String, emailCaptcha: String, newPassword: String): Response {
        RegexUtil.validPassword(newPassword).let {
            if (!it.status) {
                return Response.error(it.message)
            }
        }
        if (!RegexUtil.validEmail(email)) {
            return Response.error("邮箱格式不正确")
        }
        findByEmail(email) ?: run {
            return Response.error("邮箱不存在")
        }
        val emailLoginCaptcha = redisService.get(EMAIL_LOGIN_CAPTCHA_PREFIX + email)?.let { it as String }
            ?: return Response.error("邮箱验证码未发送或已过期")
        if (emailCaptcha != emailLoginCaptcha) {
            return Response.error("邮箱验证码错误")
        }
        val userId = (SecurityContextHolder.getContext().authentication?.principal as Jwt).getClaim<String>("userId")
        val user = findByIdWithPassword(userId) ?: return Response.error("用户不存在")
        if (user.email != email) {
            return Response.error("邮箱错误")
        }
        redisService.remove(EMAIL_LOGIN_CAPTCHA_PREFIX + email)
        user.password = passwordEncoder.encode(newPassword).toString()
        return if (updateById(user)) {
            Response.success("修改成功", user.apply { password = "" })
        } else {
            Response.error("修改失败")
        }
    }
}

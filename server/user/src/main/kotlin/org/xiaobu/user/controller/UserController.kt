package org.xiaobu.user.controller

import cn.hutool.core.date.DateUtil
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.into
import com.alibaba.fastjson2.toJSONString
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.xiaobu.core.entity.Response
import org.xiaobu.core.util.Net
import org.xiaobu.core.util.isNullOrEmpty
import org.xiaobu.user.entity.Authority
import org.xiaobu.user.entity.Role
import org.xiaobu.user.entity.User
import org.xiaobu.user.service.UserService
import java.util.*

@RestController
class UserController(private val userService: UserService) {

    @Value("\${server.port}")
    lateinit var port: String

    @PostMapping("/add")
    @PreAuthorize("@authorityExpression.hasPermission('admin')")
    fun add(@RequestBody user: User): Boolean {
        return userService.save(user)
    }

    @GetMapping("/findById")
    fun findById(@RequestParam id: String): User? {
        return userService.findById(id)
    }

    @GetMapping("/findByUsername")
    fun findByUsername(@RequestParam username: String): User? {
        return userService.findByUsername(username)
    }

    @GetMapping("/findByNickname")
    fun findByNickname(@RequestParam nickname: String): User? {
        return userService.findByNickname(nickname)
    }

    @GetMapping("/findByEmail")
    fun findByEmail(@RequestParam email: String): User? {
        return userService.findByEmail(email)
    }

    @PostMapping("/findInIds")
    fun findInIds(@RequestBody ids: MutableList<String>): MutableList<User> {
        return userService.findInIds(ids)
    }

    @GetMapping("/findByUsernameWithPassword")
    @PreAuthorize("@authorityExpression.hasPermission('admin')")
    fun findByUsernameWithPassword(@RequestParam username: String): User? {
        return userService.findByUsernameWithPassword(username)
    }

    @GetMapping("/findByEmailWithPassword")
    @PreAuthorize("@authorityExpression.hasPermission('admin')")
    fun findByEmailWithPassword(@RequestParam email: String): User? {
        return userService.findByEmailWithPassword(email)
    }

    @GetMapping("findRoleByUsername")
    fun findRoleByUsername(@RequestParam username: String): MutableList<Role> {
        return userService.findRoleByUsername(username)
    }

    @GetMapping("findAuthorityByUsername")
    fun findAuthorityByUsername(@RequestParam username: String): MutableList<Authority> {
        return userService.findAuthorityByUsername(username)
    }

    @GetMapping("findAuthorityById")
    fun findAuthorityById(@RequestParam id: String): MutableList<Authority> {
        return userService.findAuthorityById(id)
    }

    @PostMapping("/emqxAuth")
    fun emqxAuth(@RequestBody jsonObject: JSONObject): Map<String, String> {
        try {
            val res = Net.Get<String>("http://localhost:$port/getByToken") {
                addHeader("Authorization", jsonObject.getString("password"))
            }.into<Response>()
            if (res.status && res.data.toJSONString().into<User>().username == jsonObject.getString("username")) {
                return mapOf("result" to "allow")
            } else {
                return mapOf("result" to "ignore")
            }
        } catch (e: Exception) {
            return mapOf("result" to "ignore")
        }
    }

    @GetMapping("/getById")
    fun getById(@RequestParam id: String): Response {
        return userService.getById(id)
    }

    @GetMapping("/getByUsername")
    fun getByUsername(@RequestParam username: String): Response {
        return userService.getByUsername(username)
    }

    @GetMapping("/getByToken")
    fun getByToken(request: HttpServletRequest): Response {
        val userId = (SecurityContextHolder.getContext().authentication?.principal as Jwt).getClaim<String>("userId")
        val user = userService.findByIdWithPassword(userId) ?: return Response.error("用户不存在")
        if (user.updateTime < DateUtil.beginOfDay(Date())) {
            user.updateTime = Date()
            var address = ""
            if (address.isNullOrEmpty()) {
                address = "未知"
            }
            user.address = address
            userService.updateById(user)
        }
        return Response.success("获取成功", user.apply {
            password = ""
        })
    }

    @GetMapping("/updateAvatarUrl")
    fun updateAvatarUrl(@RequestParam avatarUrl: String): Response {
        return userService.updateAvatarUrl(avatarUrl)
    }

    @GetMapping("/updateNickname")
    fun updateNickname(@RequestParam nickname: String): Response {
        return userService.updateNickname(nickname)
    }

    @GetMapping("/updateEmailByPassword")
    fun updateEmailByPassword(
        @RequestParam password: String, @RequestParam newEmail: String, @RequestParam newEmailCaptcha: String
    ): Response {
        return userService.updateEmailByPassword(password, newEmail, newEmailCaptcha)
    }

    @GetMapping("/updateEmailByCaptcha")
    fun updateEmailByCaptcha(
        @RequestParam email: String,
        @RequestParam emailCaptcha: String,
        @RequestParam newEmail: String,
        @RequestParam newEmailCaptcha: String
    ): Response {
        return userService.updateEmailByCaptcha(email, emailCaptcha, newEmail, newEmailCaptcha)
    }

    @GetMapping("/updateGender")
    fun updateGender(@RequestParam gender: String): Response {
        return userService.updateGender(gender)
    }

    @GetMapping("/updateSignature")
    fun updateSignature(@RequestParam signature: String): Response {
        return userService.updateSignature(signature)
    }

    @GetMapping("/updatePasswordByPassword")
    fun updatePasswordByPassword(
        @RequestParam password: String, @RequestParam newPassword: String
    ): Response {
        return userService.updatePasswordByPassword(password, newPassword)
    }

    @GetMapping("/updatePasswordByCaptcha")
    fun updatePasswordByCaptcha(
        @RequestParam email: String, @RequestParam emailCaptcha: String, @RequestParam newPassword: String
    ): Response {
        return userService.updatePasswordByCaptcha(email, emailCaptcha, newPassword)
    }
}

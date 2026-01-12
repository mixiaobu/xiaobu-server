package org.xiaobu.user.service

import com.baomidou.mybatisplus.extension.service.IService
import org.xiaobu.core.entity.Response
import org.xiaobu.user.entity.Authority
import org.xiaobu.user.entity.Role
import org.xiaobu.user.entity.User

interface UserService : IService<User> {
    fun findById(id: String): User?
    fun findByUsername(username: String): User?
    fun findByNickname(nickname: String): User?
    fun findByEmail(email: String): User?
    fun findInIds(ids: MutableList<String>): MutableList<User>
    fun findByIdWithPassword(id: String): User?
    fun findByUsernameWithPassword(username: String): User?
    fun findByEmailWithPassword(email: String): User?
    fun findRoleByUsername(username: String): MutableList<Role>
    fun findAuthorityById(id: String): MutableList<Authority>
    fun findAuthorityByUsername(username: String): MutableList<Authority>

    fun getById(id: String): Response
    fun getByUsername(username: String): Response
    fun updateNickname(nickname: String): Response
    fun updateAvatarUrl(avatarUrl: String): Response
    fun updateEmailByPassword(
        password: String, newEmail: String, newEmailCaptcha: String
    ): Response

    fun updateEmailByCaptcha(
        email: String, emailCaptcha: String, newEmail: String, newEmailCaptcha: String
    ): Response

    fun updateGender(gender: String): Response
    fun updateSignature(signature: String): Response
    fun updatePasswordByPassword(password: String, newPassword: String): Response
    fun updatePasswordByCaptcha(email: String, emailCaptcha: String, newPassword: String): Response
}

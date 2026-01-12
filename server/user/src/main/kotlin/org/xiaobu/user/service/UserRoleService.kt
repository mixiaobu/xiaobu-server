package org.xiaobu.user.service

import com.baomidou.mybatisplus.extension.service.IService
import org.xiaobu.user.entity.UserRole

interface UserRoleService : IService<UserRole> {
    fun findByUserId(userId: String): MutableList<UserRole>
}

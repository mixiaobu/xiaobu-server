package org.xiaobu.user.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import org.xiaobu.user.entity.UserRole
import org.xiaobu.user.mapper.UserRoleMapper
import org.xiaobu.user.service.UserRoleService

@Service
class UserRoleServiceImpl : ServiceImpl<UserRoleMapper, UserRole>(), UserRoleService {

    override fun findByUserId(userId: String): MutableList<UserRole> {
        return ktQuery().eq(UserRole::userId, userId).list()
    }
}

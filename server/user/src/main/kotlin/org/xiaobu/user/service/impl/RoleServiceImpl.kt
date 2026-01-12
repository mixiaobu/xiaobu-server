package org.xiaobu.user.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import org.xiaobu.core.util.isNotNullOrEmpty
import org.xiaobu.user.entity.Role
import org.xiaobu.user.mapper.RoleMapper
import org.xiaobu.user.service.RoleService

@Service
class RoleServiceImpl : ServiceImpl<RoleMapper, Role>(), RoleService {

    override fun findInIds(ids: MutableList<String>): MutableList<Role> {
        if (ids.isNotNullOrEmpty()) {
            return ktQuery().`in`(Role::id, ids).orderByAsc(Role::sort).list()
        } else {
            return mutableListOf()
        }
    }
}

package org.xiaobu.user.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import org.xiaobu.core.util.isNotNullOrEmpty
import org.xiaobu.user.entity.RoleAuthority
import org.xiaobu.user.mapper.RoleAuthorityMapper
import org.xiaobu.user.service.RoleAuthorityService

@Service
class RoleAuthorityServiceImpl : ServiceImpl<RoleAuthorityMapper, RoleAuthority>(), RoleAuthorityService {

    override fun findInRoleIds(roleIds: MutableList<String>): MutableList<RoleAuthority> {
        if (roleIds.isNotNullOrEmpty()) {
            return ktQuery().`in`(RoleAuthority::roleId, roleIds).list()
        } else {
            return mutableListOf()
        }
    }
}

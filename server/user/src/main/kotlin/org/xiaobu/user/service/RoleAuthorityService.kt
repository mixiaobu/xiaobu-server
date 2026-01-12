package org.xiaobu.user.service

import com.baomidou.mybatisplus.extension.service.IService
import org.xiaobu.user.entity.RoleAuthority

interface RoleAuthorityService : IService<RoleAuthority> {
    fun findInRoleIds(roleIds: MutableList<String>): MutableList<RoleAuthority>
}

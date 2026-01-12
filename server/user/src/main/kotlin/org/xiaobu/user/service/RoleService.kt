package org.xiaobu.user.service

import com.baomidou.mybatisplus.extension.service.IService
import org.xiaobu.user.entity.Role

interface RoleService : IService<Role> {
    fun findInIds(ids: MutableList<String>): MutableList<Role>
}

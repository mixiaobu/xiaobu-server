package org.xiaobu.user.service

import com.baomidou.mybatisplus.extension.service.IService
import org.xiaobu.user.entity.Authority

interface AuthorityService : IService<Authority> {
    fun findInIds(ids: MutableList<String>): MutableList<Authority>
}

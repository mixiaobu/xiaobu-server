package org.xiaobu.user.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import org.xiaobu.core.util.isNotNullOrEmpty
import org.xiaobu.user.entity.Authority
import org.xiaobu.user.mapper.AuthorityMapper
import org.xiaobu.user.service.AuthorityService

@Service
class AuthorityServiceImpl : ServiceImpl<AuthorityMapper, Authority>(), AuthorityService {

    override fun findInIds(ids: MutableList<String>): MutableList<Authority> {
        if (ids.isNotNullOrEmpty()) {
            return ktQuery().`in`(Authority::id, ids).orderByAsc(Authority::sort).list()
        } else {
            return mutableListOf()
        }
    }
}

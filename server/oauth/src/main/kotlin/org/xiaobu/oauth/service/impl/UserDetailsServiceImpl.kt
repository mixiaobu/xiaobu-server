package org.xiaobu.oauth.service.impl

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.xiaobu.oauth.entity.UserDetailsImpl
import org.xiaobu.openfeign.client.UserServiceClient

@Service
class UserDetailsServiceImpl(private val userServiceClient: UserServiceClient) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetailsImpl {
        val user = userServiceClient.findByUsernameWithPassword(username) ?: userServiceClient.findByEmailWithPassword(
            username
        ) ?: throw UsernameNotFoundException("账号不存在")
        val authorityList = userServiceClient.findAuthorityByUsername(user.username)
        val authorities = authorityList.map { authority -> SimpleGrantedAuthority(authority.name) }
        return UserDetailsImpl(user, authorities)
    }
}

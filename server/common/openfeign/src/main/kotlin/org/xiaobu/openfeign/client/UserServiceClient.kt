package org.xiaobu.openfeign.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.xiaobu.core.entity.Authority
import org.xiaobu.core.entity.Role
import org.xiaobu.core.entity.User

@FeignClient("user")
interface UserServiceClient {

    @PostMapping("/add")
    fun add(@RequestBody user: User): Boolean

    @GetMapping("/findById")
    fun findById(@RequestParam id: String): User?

    @GetMapping("/findByUsername")
    fun findByUsername(@RequestParam username: String): User?

    @GetMapping("/findByNickname")
    fun findByNickname(@RequestParam nickname: String): User?

    @GetMapping("/findByEmail")
    fun findByEmail(@RequestParam email: String): User?

    @PostMapping("/findInIds")
    fun findInIds(@RequestBody ids: MutableList<String>): MutableList<User>

    @GetMapping("/findByUsernameWithPassword")
    fun findByUsernameWithPassword(@RequestParam username: String): User?

    @GetMapping("/findByEmailWithPassword")
    fun findByEmailWithPassword(@RequestParam email: String): User?

    @GetMapping("findRoleByUsername")
    fun findRoleByUsername(@RequestParam username: String): MutableList<Role>

    @GetMapping("findAuthorityByUsername")
    fun findAuthorityByUsername(@RequestParam username: String): MutableList<Authority>

    @GetMapping("findAuthorityById")
    fun findAuthorityById(@RequestParam id: String): MutableList<Authority>
}

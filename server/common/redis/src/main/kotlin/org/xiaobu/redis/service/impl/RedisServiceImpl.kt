package org.xiaobu.redis.service.impl

import jakarta.annotation.Resource
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.xiaobu.redis.service.RedisService
import java.util.concurrent.TimeUnit

@Service
class RedisServiceImpl : RedisService {

    @Resource
    lateinit var redisTemplate: RedisTemplate<String, Any>

    override fun set(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, value)
    }

    override fun set(key: String, value: Any, expireSecond: Long) {
        if (expireSecond > 0) {
            redisTemplate.opsForValue().set(key, value, expireSecond, TimeUnit.SECONDS)
        } else {
            set(key, value)
        }
    }

    override fun get(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

    override fun getAndRemove(key: String): Any? {
        return redisTemplate.opsForValue().get(key)?.also {
            redisTemplate.delete(key)
        }
    }

    override fun exist(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }

    override fun remove(key: String): Boolean {
        return redisTemplate.delete(key)
    }
}

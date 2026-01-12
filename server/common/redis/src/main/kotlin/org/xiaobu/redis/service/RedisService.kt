package org.xiaobu.redis.service

interface RedisService {
    fun set(key: String, value: Any)
    fun set(key: String, value: Any, expireSecond: Long)
    fun get(key: String): Any?
    fun getAndRemove(key: String): Any?
    fun exist(key: String): Boolean
    fun remove(key: String): Boolean
}
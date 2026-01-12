package org.xiaobu.redis.config

import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@AutoConfigureBefore(DataRedisAutoConfiguration::class)
@ComponentScan(basePackages = ["org.xiaobu.redis"])
@Configuration
class RedisConfig {

    @Bean("redisTemplate")
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = redisConnectionFactory
        val fastJsonRedisSerializer = GenericFastJsonRedisSerializer()
        //设置默认的Serialize，包含 keySerializer & valueSerializer
        redisTemplate.defaultSerializer = fastJsonRedisSerializer
        return redisTemplate
    }
}

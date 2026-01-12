package org.xiaobu.mybatis.plus.config

import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@MapperScan("org.xiaobu.**.mapper")
@Configuration
class MybatisPlusConfig {

    @Bean
    fun mybatisPlusInterceptor(): MybatisPlusInterceptor {
        val interceptor = MybatisPlusInterceptor()
        interceptor.addInnerInterceptor(PaginationInnerInterceptor(DbType.MYSQL))
        return interceptor
    }

    @Bean
    fun myBatisPlusConfigurationCustomizer(): ConfigurationCustomizer {
        return ConfigurationCustomizer { configuration ->
            val typeHandlerRegistry = configuration.typeHandlerRegistry
            typeHandlerRegistry.register(JsonTypeHandler::class.java)
        }
    }

    @Bean
    fun jsonTypeHandler(): JsonTypeHandler {
        return JsonTypeHandler()
    }
}

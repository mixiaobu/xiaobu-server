package org.xiaobu.openfeign.config

import feign.RequestInterceptor
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableFeignClients(basePackages = ["org.xiaobu.openfeign.client"])
@Configuration
class OpenfeignConfig {

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return IgnoreAuthRequestInterceptor()
    }
}

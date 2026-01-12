package org.xiaobu.openfeign.config

import feign.RequestInterceptor
import feign.RequestTemplate
import org.xiaobu.core.constant.FeignConstants

class IgnoreAuthRequestInterceptor : RequestInterceptor {

    override fun apply(requestTemplate: RequestTemplate) {
        requestTemplate.removeHeader(FeignConstants.IGNORE_AUTH_HEADER_KEY)
        requestTemplate.header(FeignConstants.IGNORE_AUTH_HEADER_KEY, FeignConstants.IGNORE_AUTH_HEADER_VALUE)
    }
}

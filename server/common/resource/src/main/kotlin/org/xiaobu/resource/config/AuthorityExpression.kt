package org.xiaobu.resource.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.xiaobu.core.constant.FeignConstants

@Component
class AuthorityExpression(private val request: HttpServletRequest) {

    fun hasPermission(permission: String): Boolean {
        val ignoreHeaders = request.getHeaders(FeignConstants.IGNORE_AUTH_HEADER_KEY)
        if (ignoreHeaders != null && ignoreHeaders.hasMoreElements()) {
            while (ignoreHeaders.hasMoreElements()) {
                val ignoreHeader = ignoreHeaders.nextElement()
                if (FeignConstants.IGNORE_AUTH_HEADER_VALUE == ignoreHeader) {
                    return true
                }
            }
        }
        return SecurityContextHolder.getContext().authentication?.authorities?.any { it.authority == permission }
            ?: false
    }
}

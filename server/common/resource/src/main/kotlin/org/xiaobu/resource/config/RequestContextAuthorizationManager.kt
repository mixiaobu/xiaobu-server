package org.xiaobu.resource.config

import org.springframework.security.authorization.AuthenticatedAuthorizationManager
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.authorization.AuthorizationResult
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.xiaobu.core.constant.FeignConstants
import org.xiaobu.core.util.isNotNullOrEmpty
import org.xiaobu.resource.properties.Request
import java.util.function.Supplier

class RequestContextAuthorizationManager(private val request: Request) : AuthorizationManager<RequestAuthorizationContext> {

    override fun authorize(
        authentication: Supplier<out Authentication>, requestContext: RequestAuthorizationContext
    ): AuthorizationResult? {
        val request = requestContext.request
        val ignoreHeaders = request.getHeaders(FeignConstants.IGNORE_AUTH_HEADER_KEY)
        if (ignoreHeaders.isNotNullOrEmpty()) {
            while (ignoreHeaders.hasMoreElements()) {
                val ignoreHeader = ignoreHeaders.nextElement()
                if (FeignConstants.IGNORE_AUTH_HEADER_VALUE == ignoreHeader) {
                    return AuthorizationDecision(java.lang.Boolean.TRUE)
                }
            }
        }
        val requestURI = request.requestURI
        val contextPath = request.contextPath
        val requestPath = if (contextPath.isNotNullOrEmpty()) {
            requestURI.replaceFirst(contextPath.toRegex(), "")
        } else {
            requestURI
        }
        if (this.request.inWhite(requestPath)) {
            return AuthorizationDecision(java.lang.Boolean.TRUE)
        }
        return AuthenticatedAuthorizationManager.authenticated<Any>().authorize(authentication, requestContext)
    }
}

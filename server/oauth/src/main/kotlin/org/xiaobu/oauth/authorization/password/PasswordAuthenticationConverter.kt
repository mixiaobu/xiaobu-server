package org.xiaobu.oauth.authorization.password

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.util.StringUtils
import org.xiaobu.oauth.authorization.GrantType
import org.xiaobu.oauth.authorization.OAuth2ParameterName

class PasswordAuthenticationConverter : AuthenticationConverter {

    override fun convert(request: HttpServletRequest): Authentication? {
        val grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE)
        if (GrantType.PASSWORD.value != grantType) {
            return null
        }
        val username = request.getParameter(OAuth2ParameterName.USERNAME)
        val password = request.getParameter(OAuth2ParameterName.PASSWORD)
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return null
        }
        val scope = request.getParameter(OAuth2ParameterNames.SCOPE)
        val scopes = if (StringUtils.hasText(scope)) {
            scope.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.toHashSet()
        } else {
            hashSetOf()
        }
        val clientPrincipal = SecurityContextHolder.getContext().authentication ?: throw OAuth2AuthenticationException(
            OAuth2ErrorCodes.INVALID_CLIENT
        )
        return PasswordAuthenticationToken(
            clientPrincipal = clientPrincipal,
            username = username,
            password = password,
            scopes = scopes,
            additionalParameters = emptyMap()
        )
    }
}

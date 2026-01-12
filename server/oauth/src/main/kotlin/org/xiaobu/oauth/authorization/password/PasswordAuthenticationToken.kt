package org.xiaobu.oauth.authorization.password

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken
import org.xiaobu.oauth.authorization.GrantType

class PasswordAuthenticationToken(
    clientPrincipal: Authentication,
    val username: String,
    val password: String,
    val scopes: HashSet<String>,
    additionalParameters: Map<String, Any>
) : OAuth2AuthorizationGrantAuthenticationToken(
    GrantType.PASSWORD, clientPrincipal, additionalParameters
)

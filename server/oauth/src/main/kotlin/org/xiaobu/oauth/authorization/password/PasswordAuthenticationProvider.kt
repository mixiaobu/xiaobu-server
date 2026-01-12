package org.xiaobu.oauth.authorization.password

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.*
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator
import org.xiaobu.oauth.authorization.GrantType
import java.security.Principal

class PasswordAuthenticationProvider(
    private val authenticationManager: AuthenticationManager,
    private val authorizationService: OAuth2AuthorizationService,
    private val tokenGenerator: OAuth2TokenGenerator<*>
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val passwordAuth = authentication as PasswordAuthenticationToken
        val registeredClient = (passwordAuth.principal as? OAuth2ClientAuthenticationToken)?.registeredClient
            ?: throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT)
        if (!registeredClient.authorizationGrantTypes.contains(GrantType.PASSWORD)) {
            throw OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT)
        }
        val principal = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(passwordAuth.username, passwordAuth.password)
        )
        val tokenContextBuilder =
            DefaultOAuth2TokenContext.builder().registeredClient(registeredClient).principal(principal)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(passwordAuth.scopes).authorizationGrantType(GrantType.PASSWORD)
                .authorizationGrant(passwordAuth)
        val authorizationBuilder =
            OAuth2Authorization.withRegisteredClient(registeredClient).authorizedScopes(passwordAuth.scopes)
                .principalName(principal.name).attribute(Principal::class.java.name, principal)
                .authorizationGrantType(GrantType.PASSWORD)
        val accessToken = generateAccessToken(tokenContextBuilder, authorizationBuilder)
        val refreshToken = generateRefreshToken(tokenContextBuilder, authorizationBuilder, registeredClient)
        val idToken = generateOidcIdToken(tokenContextBuilder, authorizationBuilder, passwordAuth.scopes)
        val authorization = authorizationBuilder.build()
        authorizationService.save(authorization)
        val additionalParameters: MutableMap<String, Any> = HashMap(1)
        if (idToken != null) {
            additionalParameters[OidcParameterNames.ID_TOKEN] = idToken.tokenValue
        }
        return OAuth2AccessTokenAuthenticationToken(
            registeredClient, principal, accessToken, refreshToken, additionalParameters
        )
    }

    private fun generateAccessToken(
        tokenContextBuilder: DefaultOAuth2TokenContext.Builder, authorizationBuilder: OAuth2Authorization.Builder
    ): OAuth2AccessToken {
        val accessTokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build()
        val generatedAccessToken =
            tokenGenerator.generate(accessTokenContext) ?: throw OAuth2AuthenticationException("生成访问令牌失败")
        val accessToken = OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            generatedAccessToken.tokenValue,
            generatedAccessToken.issuedAt,
            generatedAccessToken.expiresAt,
            accessTokenContext.authorizedScopes
        )
        if (generatedAccessToken is ClaimAccessor) {
            authorizationBuilder.token(accessToken) { metadata ->
                metadata[OAuth2Authorization.Token.CLAIMS_METADATA_NAME] = generatedAccessToken.claims
            }
        } else {
            authorizationBuilder.accessToken(accessToken)
        }
        return accessToken
    }

    private fun generateRefreshToken(
        tokenContextBuilder: DefaultOAuth2TokenContext.Builder,
        authorizationBuilder: OAuth2Authorization.Builder,
        registeredClient: RegisteredClient
    ): OAuth2RefreshToken? {
        var refreshToken: OAuth2RefreshToken? = null
        if (registeredClient.authorizationGrantTypes.contains(AuthorizationGrantType.REFRESH_TOKEN)) {
            val refreshTokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build()
            val generatedRefreshToken =
                tokenGenerator.generate(refreshTokenContext) ?: throw OAuth2AuthenticationException("生成刷新令牌失败")
            if (generatedRefreshToken is OAuth2RefreshToken) {
                refreshToken = generatedRefreshToken
                authorizationBuilder.refreshToken(refreshToken)
            }
        }
        return refreshToken
    }

    private fun generateOidcIdToken(
        tokenContextBuilder: DefaultOAuth2TokenContext.Builder,
        authorizationBuilder: OAuth2Authorization.Builder,
        scopes: Set<String>,
    ): OidcIdToken? {
        var idToken: OidcIdToken? = null
        if (scopes.contains(OidcScopes.OPENID)) {
            val idTokenContext = tokenContextBuilder.tokenType(OAuth2TokenType(OidcParameterNames.ID_TOKEN))
                .authorization(authorizationBuilder.build()).build()
            val generatedIdToken = tokenGenerator.generate(idTokenContext)
            if (generatedIdToken !is Jwt) {
                throw OAuth2AuthenticationException("生成 ID 令牌失败")
            }
            idToken = OidcIdToken(
                generatedIdToken.tokenValue,
                generatedIdToken.issuedAt,
                generatedIdToken.expiresAt,
                generatedIdToken.claims
            )
            authorizationBuilder.token(idToken) { metadata ->
                metadata[OAuth2Authorization.Token.CLAIMS_METADATA_NAME] = idToken.claims
            }
        }
        return idToken
    }

    override fun supports(authentication: Class<*>): Boolean {
        return PasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}

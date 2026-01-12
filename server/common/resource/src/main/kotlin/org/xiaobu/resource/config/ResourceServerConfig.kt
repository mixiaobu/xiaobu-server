package org.xiaobu.resource.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.security.web.SecurityFilterChain
import org.springframework.util.StringUtils
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.xiaobu.core.entity.Response
import org.xiaobu.core.util.log
import org.xiaobu.openfeign.client.UserServiceClient
import org.xiaobu.resource.properties.Request
import org.xiaobu.web.util.write
import java.util.regex.Pattern

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(Request::class)
@AutoConfigureBefore(OAuth2ResourceServerAutoConfiguration::class)
class ResourceServerConfig(
    private val request: Request,
    private val httpServletRequest: HttpServletRequest,
    private val userServiceClient: UserServiceClient
) {

    @Bean
    fun authorityExpression(): AuthorityExpression {
        return AuthorityExpression(httpServletRequest)
    }

    @Bean
    fun requestContextAuthorizationManager(): RequestContextAuthorizationManager {
        return RequestContextAuthorizationManager(request)
    }

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors(Customizer.withDefaults())
        http.csrf { it.disable() }
        http.authorizeHttpRequests {
            it.anyRequest().access(requestContextAuthorizationManager())
        }
        http.oauth2ResourceServer {
            it.accessDeniedHandler { request, response, accessDeniedException ->
                log.error(accessDeniedException.message)
                response.write(Response.unauthorized("无权限"))
            }.authenticationEntryPoint { request, response, authException ->
                log.error(authException.message)
                response.write(Response.expired("未登录"))
            }
            it.bearerTokenResolver(object : BearerTokenResolver {
                val authorizationPattern: Pattern =
                    Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE)

                override fun resolve(request: HttpServletRequest): String? {
                    val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
                    if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
                        return null
                    }
                    val matcher = authorizationPattern.matcher(authorization)
                    if (!matcher.matches()) {
                        return null
                    }
                    return matcher.group("token")
                }
            })
        }
        http.oauth2ResourceServer { resourceServer ->
            resourceServer.jwt {
                it.jwtAuthenticationConverter(Converter<Jwt, AbstractAuthenticationToken> { source ->
                    val authorityList = userServiceClient.findAuthorityById(source.claims["userId"].toString())
                    val authorities = authorityList.map { authority -> SimpleGrantedAuthority(authority.name) }
                    JwtAuthenticationToken(source, authorities)
                })
            }
        }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOriginPattern("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        config.allowCredentials = true
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @ConditionalOnMissingBean(PasswordEncoder::class)
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

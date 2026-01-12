package org.xiaobu.oauth.config

import com.baomidou.mybatisplus.core.toolkit.IdWorker
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.jackson.SecurityJacksonModules
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.security.oauth2.server.authorization.token.*
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.DelegatingAuthenticationConverter
import org.xiaobu.oauth.authorization.GrantType
import org.xiaobu.oauth.authorization.password.PasswordAuthenticationConverter
import org.xiaobu.oauth.authorization.password.PasswordAuthenticationProvider
import org.xiaobu.oauth.entity.UserDetailsImpl
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import java.security.KeyStore
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration

@Configuration
@EnableWebSecurity
class AuthorizationConfig {

    @Value("\${app.issuer-uri}")
    lateinit var issuerUrl: String

    @Bean
    @Throws(Exception::class)
    fun authorizationServerSecurityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager,
        authorizationService: OAuth2AuthorizationService,
        tokenGenerator: OAuth2TokenGenerator<*>
    ): SecurityFilterChain {
        val authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer()
        authorizationServerConfigurer.tokenEndpoint {
            it.accessTokenRequestConverter(
                DelegatingAuthenticationConverter(
                    listOf(
                        PasswordAuthenticationConverter(),
                        OAuth2ClientCredentialsAuthenticationConverter(),
                        OAuth2AuthorizationCodeAuthenticationConverter(),
                        OAuth2RefreshTokenAuthenticationConverter()
                    )
                )
            ).authenticationProvider(
                PasswordAuthenticationProvider(
                    authenticationManager, authorizationService, tokenGenerator
                )
            )
        }
        http.securityMatcher(authorizationServerConfigurer.endpointsMatcher).authorizeHttpRequests {
            it.anyRequest().authenticated()
        }.with(authorizationServerConfigurer) {
            it.oidc(Customizer.withDefaults())
        }
        return http.build()
    }

    @ConditionalOnMissingBean(PasswordEncoder::class)
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun registeredClientRepository(
        jdbcTemplate: JdbcTemplate, passwordEncoder: PasswordEncoder
    ): RegisteredClientRepository {
        val registeredClientRepository = JdbcRegisteredClientRepository(jdbcTemplate)
        val registeredClientBuild = RegisteredClient.withId(IdWorker.getIdStr()).clientId("xiaobu")
            .clientSecret(passwordEncoder.encode("xiaobu")).clientName("小布")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN).authorizationGrantType(GrantType.PASSWORD)
            .scope(OidcScopes.OPENID).redirectUri("http://172.16.1.2:3000/oauth2Redirect")
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build()).tokenSettings(
                TokenSettings.builder().refreshTokenTimeToLive(Duration.ofDays(365))
                    .accessTokenTimeToLive(Duration.ofDays(7)).build()
            )
        val registeredClient1 = registeredClientRepository.findByClientId(registeredClientBuild.build().clientId)
        if (registeredClient1 != null) {
            registeredClientBuild.id(registeredClient1.id)
            registeredClientRepository.save(registeredClientBuild.build())
        } else {
            registeredClientRepository.save(registeredClientBuild.build())
        }
        return registeredClientRepository
    }

    @Bean
    fun authorizationService(
        jdbcTemplate: JdbcTemplate, registeredClientRepository: RegisteredClientRepository
    ): OAuth2AuthorizationService {
        val service = JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository)
        val typeValidatorBuilder = BasicPolymorphicTypeValidator.builder().allowIfSubType(UserDetailsImpl::class.java)
        val mapper = JsonMapper.builder()
            .addModules(SecurityJacksonModules.getModules(javaClass.classLoader, typeValidatorBuilder)).build()
        val oAuth2AuthorizationRowMapper = JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationRowMapper(
            registeredClientRepository, mapper
        )
        service.setAuthorizationRowMapper(oAuth2AuthorizationRowMapper)
        return service
    }

    @Bean
    fun authorizationConsentService(
        jdbcTemplate: JdbcTemplate, registeredClientRepository: RegisteredClientRepository
    ): OAuth2AuthorizationConsentService {
        return JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository)
    }

    @Bean
    fun authenticationManager(
        http: HttpSecurity
    ): AuthenticationManager {
        return http.getSharedObject(AuthenticationManagerBuilder::class.java).build()
    }

    @Bean
    fun tokenGenerator(jwtEncoder: JwtEncoder): OAuth2TokenGenerator<*> {
        val jwtGenerator = JwtGenerator(jwtEncoder)
        jwtGenerator.setJwtCustomizer { context ->
            val userDetails = context.getPrincipal<Authentication>().principal as UserDetailsImpl
            context.claims.claims { existingClaims ->
                existingClaims["userId"] = userDetails.user.id
            }
        }
        val accessTokenGenerator = OAuth2AccessTokenGenerator()
        accessTokenGenerator.setAccessTokenCustomizer { context ->
            val userDetails = context.getPrincipal<Authentication>().principal as UserDetailsImpl
            context.claims.claims { existingClaims ->
                existingClaims["userId"] = userDetails.user.id
            }
        }
        val refreshTokenGenerator = OAuth2RefreshTokenGenerator()
        return DelegatingOAuth2TokenGenerator(
            jwtGenerator, accessTokenGenerator, refreshTokenGenerator
        )
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyStore = KeyStore.getInstance("JKS")
        val keyStorePath = "xiaobu.jks"
        val keyStorePassword = System.getProperty("keystore.password").toCharArray()
        val keyAlias = "xiaobu"
        val keyPassword = System.getProperty("keystore.password").toCharArray()
        keyStore.load(ClassPathResource(keyStorePath).inputStream, keyStorePassword)
        val privateKey = keyStore.getKey(keyAlias, keyPassword) as RSAPrivateKey
        val certificate = keyStore.getCertificate(keyAlias)
        val publicKey = certificate.publicKey as RSAPublicKey
        val rsaKey = RSAKey.Builder(publicKey).privateKey(privateKey).keyID(keyAlias).build()
        val jwkSet = JWKSet(rsaKey)
        return ImmutableJWKSet(jwkSet)
    }

    @Bean
    fun jwtEncoder(jwkSource: JWKSource<SecurityContext>): JwtEncoder {
        return NimbusJwtEncoder(jwkSource)
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder().issuer(issuerUrl).build()
    }
}

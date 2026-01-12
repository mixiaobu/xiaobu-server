package org.xiaobu.oauth.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher

@Component
@ConfigurationProperties(prefix = "request")
class Request {

    val pathMatcher = AntPathMatcher()

    lateinit var whites: List<String>

    fun inWhite(url: String): Boolean {
        return whites.any { pathMatcher.match(it, url) }
    }
}

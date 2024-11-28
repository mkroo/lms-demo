package com.mkroo.lmsdemo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val expiresInMills: Long
) {
    val expiresIn: Duration = Duration.ofMillis(expiresInMills)
}

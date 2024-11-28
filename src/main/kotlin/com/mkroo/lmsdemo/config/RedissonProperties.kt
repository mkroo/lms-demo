package com.mkroo.lmsdemo.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "lock-client.redisson")
data class RedissonProperties(
    val address: String,
    val database: Int,
)

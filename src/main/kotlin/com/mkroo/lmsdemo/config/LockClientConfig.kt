package com.mkroo.lmsdemo.config

import com.mkroo.lmsdemo.application.LockClient
import com.mkroo.lmsdemo.infrastructure.lockclient.InMemoryLockClient
import com.mkroo.lmsdemo.infrastructure.lockclient.RedissonLockClient
import org.redisson.Redisson
import org.redisson.config.Config
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@EnableConfigurationProperties(RedissonProperties::class)
class LockClientConfig {
    @Bean
    @Profile("!test")
    fun redisLockClient(properties: RedissonProperties): LockClient {
        val config = Config().apply {
            useSingleServer().apply {
                address = properties.address
                database = properties.database
            }
        }

        val redissonClient = Redisson.create(config)

        return RedissonLockClient(redissonClient)
    }

    @Bean
    @Profile("test")
    fun inMemoryLockClient(): LockClient {
        return InMemoryLockClient()
    }
}

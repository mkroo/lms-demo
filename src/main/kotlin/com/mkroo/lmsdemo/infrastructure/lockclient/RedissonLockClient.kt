package com.mkroo.lmsdemo.infrastructure.lockclient

import com.mkroo.lmsdemo.application.LockClient
import org.redisson.api.RedissonClient
import java.time.Duration
import java.util.concurrent.TimeUnit

class RedissonLockClient(private val redissonClient: RedissonClient) : LockClient() {
    override fun tryLock(key: String, waitTimeout: Duration, leaseTimeout: Duration): Boolean {
        return getLock(key).tryLock(waitTimeout.toMillis(), leaseTimeout.toMillis(), TimeUnit.MILLISECONDS)
    }

    override fun unlock(key: String) {
        getLock(key).unlock()
    }

    private fun getLock(key: String) = redissonClient.getLock(key)
}

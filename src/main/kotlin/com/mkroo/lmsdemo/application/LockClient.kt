package com.mkroo.lmsdemo.application

import java.time.Duration

abstract class LockClient {
    protected abstract fun tryLock(key: String, waitTimeout: Duration, leaseTimeout: Duration): Boolean
    protected abstract fun unlock(key: String)

    fun <R>tryLock(key: String, waitTimeout: Duration, leaseTimeout: Duration, process: () -> R) : R {
        if (tryLock(key, waitTimeout, leaseTimeout)) {
            return try {
                process()
            } catch (e: Exception) {
                throw e
            } finally {
                unlock(key)
            }
        } else {
            throw IllegalStateException("Failed to get lock: $key")
        }
    }
}

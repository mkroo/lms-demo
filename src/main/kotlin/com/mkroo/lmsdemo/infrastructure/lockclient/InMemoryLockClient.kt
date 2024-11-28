package com.mkroo.lmsdemo.infrastructure.lockclient

import com.mkroo.lmsdemo.application.LockClient
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class InMemoryLockClient : LockClient() {
    private val locks = ConcurrentHashMap<String, ReentrantLock>()
    private val scheduler = Executors.newScheduledThreadPool(1)

    override fun tryLock(key: String, waitTimeout: Duration, leaseTimeout: Duration): Boolean {
        val lock = getLock(key)

        scheduler.schedule({
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }, leaseTimeout.toMillis(), TimeUnit.MILLISECONDS)

        return lock.tryLock(waitTimeout.toMillis(), TimeUnit.MILLISECONDS)
    }

    override fun unlock(key: String) {
        getLock(key).unlock()
    }

    private fun getLock(key: String) = locks.computeIfAbsent(key) { ReentrantLock() }
}

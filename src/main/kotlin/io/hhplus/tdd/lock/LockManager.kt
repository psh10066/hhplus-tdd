package io.hhplus.tdd.lock

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class LockManager {

    private val lockMap = ConcurrentHashMap<Long, ReentrantLock>()

    fun <T> withLock(id: Long, block: () -> T): T {
        val lock = lockMap.computeIfAbsent(id) { ReentrantLock(true) }

        lock.withLock {
            return block()
        }
    }
}
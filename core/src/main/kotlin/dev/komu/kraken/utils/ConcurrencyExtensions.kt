package dev.komu.kraken.utils

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock

inline fun <T> Lock.relinquish(callback: () -> T): T =
    try {
        unlock()
        callback()
    } finally {
        lock()
    }

fun WriteLock.yieldLock() {
    if (isHeldByCurrentThread) {
        unlock()
        lock()
    }
}

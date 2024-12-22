package io.hhplus.tdd.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ReentrantLockManager {
    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    private ReentrantLock getLock(Long id) {
        return userLocks.computeIfAbsent(id, key -> new ReentrantLock(true));
    }

    public void aquireLock(Long id) {
        getLock(id).lock();
    }

    public void releaseLock(Long id) {
        ReentrantLock lock = getLock(id);
        lock.unlock();
        if (!lock.hasQueuedThreads()) {
            userLocks.remove(id);
        }
    }
}

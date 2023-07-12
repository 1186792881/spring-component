package com.wangyi.component.redisson.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁工具
 */
@Component
public class DistributedLockUtil {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public DistributedLockUtil() {
        log.info("分布式锁初始化成功");
    }

    @Resource
    private RedissonClient redissonClient;

    /**
     * redisson 锁
     * @param key 锁的key
     * @param waitTime 获取锁最大等待时间
     * @param unit 时间单位
     * @param supplier 要加锁的执行逻辑
     * @return
     * @param <T>
     */
    public <T> T tryLockWithThrows(String key, long waitTime, TimeUnit unit, SupplierThrow<T> supplier) throws Throwable {
        RLock lock = redissonClient.getLock(key);
        try {
            boolean lockSuccess = lock.tryLock(waitTime, unit);
            if (!lockSuccess) {
                throw new DistributedLockException("获取锁失败, key:" + key);
            }
            return supplier.get();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new DistributedLockException(e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * redisson 锁
     * @param key 锁的key
     * @param waitTime 获取锁最大等待时间
     * @param unit 时间单位
     * @param supplier 要加锁的执行逻辑
     * @return
     * @param <T>
     */
    public <T> T tryLock(String key, long waitTime, TimeUnit unit, Supplier<T> supplier) {
        try {
            return tryLockWithThrows(key, waitTime, unit, supplier::get);
        } catch (DistributedLockException e) {
            throw e;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}

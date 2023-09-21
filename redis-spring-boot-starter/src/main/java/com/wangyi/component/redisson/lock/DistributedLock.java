package com.wangyi.component.redisson.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * key前缀, 默认取方法全限定名, 除非在不同方法上对同一个资源加锁, 就自己指定
     * @return
     */
    String keyPrefix() default "";

    /**
     * 锁的key
     * @return
     */
    String key() default "";

    /**
     * 获取锁等待时间
     * 默认-1, 不等待, 获取不到锁直接失败
     * @return
     */
    int waitTime() default -1;

    /**
     * 锁时间单位
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}

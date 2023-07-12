package com.wangyi.component.redisson.lock;

@FunctionalInterface
public interface SupplierThrow<T> {

    T get() throws Throwable;
}

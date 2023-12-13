package com.wangyi.component.uid.core.resposity;

import com.wangyi.component.uid.core.impl.CachedUidGenerator;

/**
 * WorkerId 分配策略
 * @author DengJun 2021/5/11
 */
public interface AssignerMode {
    String DEFAULT = "default";
    String REDIS = "redis";

    /**
     * 生成器模式
     */
    enum Generator {
        /**
         * 使用基础生成器（默认）
         */
        none,
        /**
         * 使用内存生成器 {@link CachedUidGenerator}
         */
        memory,
    }

    /**
     * 节点分配器模式
     */
    enum Assigner {
        /**
         * 默认使用随机生成 workerId
         */
        none,
        /**
         * 使用REDIS生成工作节点ID
         */
        redis,
        /**
         * 使用MySQL生成工作节点ID
         */
        db
    }
}

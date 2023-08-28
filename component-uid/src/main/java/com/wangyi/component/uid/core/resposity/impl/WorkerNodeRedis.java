package com.wangyi.component.uid.core.resposity.impl;

import com.wangyi.component.uid.core.config.UidProperties;
import com.wangyi.component.uid.core.exception.UidGenerateException;
import com.wangyi.component.uid.core.resposity.WorkerNodeEntity;
import com.wangyi.component.uid.core.resposity.WorkerNodeResposity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

public class WorkerNodeRedis implements WorkerNodeResposity {

    private static final Logger LOGGER = LoggerFactory.getLogger( WorkerNodeRedis.class );


    private final StringRedisTemplate stringRedisTemplate;
    private final long maxWorkerId;
    private final String workerIdKey;

    public WorkerNodeRedis(StringRedisTemplate stringRedisTemplate, UidProperties uidProperties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.maxWorkerId = ~(-1L << uidProperties.getWorkerBits());
        this.workerIdKey = uidProperties.getRedis().getWorkerIdKey();
        LOGGER.info("uid-redis-generator workerIdBits:{}, maxWorkerId: {}, workerIdKey: {}", uidProperties.getWorkerBits(), maxWorkerId, workerIdKey);
    }

    @Override
    public WorkerNodeEntity getWorkerNodeByHostPort(String host, String port) {
        return null;
    }

    @Override
    public void addWorkerNode(WorkerNodeEntity entity) {
        Long workerId = stringRedisTemplate.opsForValue().increment( workerIdKey );
        if (workerId == null) {
            throw new UidGenerateException("uid-redis-generator 生成workerId失败");
        }
        if (workerId > maxWorkerId) {
            LOGGER.warn("uid-redis-generator workerId: {}, 超过最大workerId: {}", workerId, maxWorkerId);
            workerId = workerId % maxWorkerId;
        }
        LOGGER.info("uid-redis-generator workerId: {}", workerId);
        entity.setId( workerId );
    }

}

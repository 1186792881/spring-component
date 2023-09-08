package com.wangyi.component.uid.core.resposity.impl;

import com.wangyi.component.uid.core.config.UidProperties;
import com.wangyi.component.uid.core.resposity.WorkerNodeEntity;
import com.wangyi.component.uid.core.resposity.WorkerNodeResposity;
import com.wangyi.component.uid.core.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class WorkerNodeDefault implements WorkerNodeResposity {

    private static final Logger LOGGER = LoggerFactory.getLogger( WorkerNodeDefault.class );

    private final long maxWorkerId;

    public WorkerNodeDefault(UidProperties uidProperties) {
        this.maxWorkerId = ~(-1L << uidProperties.getWorkerBits());
        LOGGER.info("uid-default-generator workerIdBits:{}, maxWorkerId: {}", uidProperties.getWorkerBits(), maxWorkerId);
    }

    @Override
    public WorkerNodeEntity getWorkerNodeByHostPort(String host, String port) {
        return null;
    }

    @Override
    public void addWorkerNode(WorkerNodeEntity entity) {
        long workerId = 1L;
        String hosName = entity.getHostName();
        String port = entity.getPort();

        int hashCode = Objects.hash(hosName, port);
        if (hashCode > 0 && hashCode < maxWorkerId) {
            workerId = hashCode;
        } else {
            workerId = Math.abs(hashCode) % (maxWorkerId + 1);
        }

        if (workerId <= 0) {
            workerId = RandomUtils.randomInt(1, (int) maxWorkerId);
        }

        LOGGER.info("uid-default-generator  hashCode: {}, 自动生成workerId: {}", hashCode, workerId);
        entity.setId( workerId );
    }

}

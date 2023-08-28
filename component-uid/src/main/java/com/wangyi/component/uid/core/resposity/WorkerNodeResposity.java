package com.wangyi.component.uid.core.resposity;

public interface WorkerNodeResposity {
    WorkerNodeEntity getWorkerNodeByHostPort(String host, String port);

    void addWorkerNode(WorkerNodeEntity entity);
}

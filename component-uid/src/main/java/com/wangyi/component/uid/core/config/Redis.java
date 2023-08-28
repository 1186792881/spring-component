package com.wangyi.component.uid.core.config;

public class Redis {

    /**
     * 存放workerId的 key
     */
    private String workerIdKey = "uid:workerId";

    public String getWorkerIdKey() {
        return workerIdKey;
    }

    public void setWorkerIdKey(String workerIdKey) {
        this.workerIdKey = workerIdKey;
    }

}

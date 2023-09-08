package com.wangyi.component.uid.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 86177
 */
@ConfigurationProperties(prefix = "uid")
public class UidProperties {

    /**
     * worker id 分配策略
     * 默认 none
     */
    private String assignerMode;

    /**
     * 当前时间，相对于时间基点 epochStr 的增量值，单位：秒，最多可支持约 17 年
     * 2^29 / (60 * 60 * 24 * 365) = 17年
     */
    private int timeBits = 29;

    /**
     * 机器id，最多可支持约 209W 次机器启动。内置实现为在启动时由数据库分配，默认分配策略为用后即弃，后续可提供复用策略。
     * 2^21 = 2097152 (大约 209 万次机器启动)
     */
    private int workerBits = 21;

    /**
     * 每秒下的并发序列，13 bits可支持每秒8192个并发。
     * 2^13 = 8192
     */
    private int seqBits = 13;

    /**
     * 时间基点. 例如 "2023-08-10";
     */
    private String epochStr = "2023-08-10";

    /**
     * RingBuffer size扩容参数, 可提高UID生成的吞吐量.
     * 默认:3， 原bufferSize=8192, 扩容后bufferSize= 8192 << 3 = 65536
     */
    private int boostPower = 3;

    /**
     * 指定何时向RingBuffer中填充UID, 取值为百分比(0, 100), 默认为50
     * 举例: bufferSize=1024, paddingFactor=50 -> threshold=1024 * 50 / 100 = 512.
     * 当环上可用UID数量 < 512时, 将自动对RingBuffer进行填充补全
     */
    private int paddingFactor = 50;

    /**
     * 另外一种RingBuffer填充时机, 在Schedule线程中, 周期性检查填充
     * 默认:不配置此项, 即不使用Schedule线程. 如需使用, 请指定Schedule线程时间间隔, 单位:秒
     */
    private long scheduleInterval = 0;

    /**
     * 独立数据源配置
     */
    private DataSource dataSource = new DataSource();

    /**
     * redis 配置
     */
    private Redis redis = new Redis();

    public String getAssignerMode() {
        return assignerMode;
    }

    public void setAssignerMode(String assignerMode) {
        this.assignerMode = assignerMode;
    }

    public int getTimeBits() {
        return timeBits;
    }

    public void setTimeBits(int timeBits) {
        this.timeBits = timeBits;
    }

    public int getWorkerBits() {
        return workerBits;
    }

    public void setWorkerBits(int workerBits) {
        this.workerBits = workerBits;
    }

    public int getSeqBits() {
        return seqBits;
    }

    public void setSeqBits(int seqBits) {
        this.seqBits = seqBits;
    }

    public String getEpochStr() {
        return epochStr;
    }

    public void setEpochStr(String epochStr) {
        this.epochStr = epochStr;
    }

    public int getBoostPower() {
        return boostPower;
    }

    public void setBoostPower(int boostPower) {
        this.boostPower = boostPower;
    }

    public int getPaddingFactor() {
        return paddingFactor;
    }

    public void setPaddingFactor(int paddingFactor) {
        this.paddingFactor = paddingFactor;
    }

    public long getScheduleInterval() {
        return scheduleInterval;
    }

    public void setScheduleInterval(long scheduleInterval) {
        this.scheduleInterval = scheduleInterval;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }
}

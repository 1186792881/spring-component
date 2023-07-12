package com.wangyi.component.redisson.delay;

/**
 * 延迟消息
 */
public class DelayMessage {

    /**
     * 消息要发送到的队列
     */
    private String topic;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 延迟时间, 单位秒
     */
    private long delayTime;

    /**
     * 消息创建时间
     */
    private long timeStamp;

    /**
     * 重试次数
     */
    private int retry = 0;

    public DelayMessage() {
    }

    public DelayMessage(String topic, String message, long delayTime) {
        this.topic = topic;
        this.message = message;
        this.delayTime = delayTime;
        this.timeStamp = System.currentTimeMillis();
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    @Override
    public String toString() {
        return "DelayMessage{" +
                "topic='" + topic + '\'' +
                ", message='" + message + '\'' +
                ", delayTime=" + delayTime +
                ", timeStamp=" + timeStamp +
                ", retry=" + retry +
                '}';
    }
}

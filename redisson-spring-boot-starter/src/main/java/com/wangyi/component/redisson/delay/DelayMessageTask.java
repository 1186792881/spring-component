package com.wangyi.component.redisson.delay;

import org.springframework.context.ApplicationContext;

import java.util.List;

public class DelayMessageTask implements Runnable {

    private List<DelayMessage> delayMessage;
    private ApplicationContext applicationContext;

    public DelayMessageTask(ApplicationContext applicationContext, List<DelayMessage> delayMessage) {
        this.applicationContext = applicationContext;
        this.delayMessage = delayMessage;
    }

    public List<DelayMessage> getDelayMessage() {
        return delayMessage;
    }

    @Override
    public void run() {
        applicationContext.publishEvent(new DelayMessageEvent(this, delayMessage));
    }

}

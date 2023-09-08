package com.wangyi.component.redisson.delay;

import org.springframework.context.ApplicationEvent;

import java.util.List;

public class DelayMessageEvent extends ApplicationEvent {

    private List<DelayMessage> delayMessage;

    public DelayMessageEvent(Object source, List<DelayMessage> delayMessage) {
        super(source);
        this.delayMessage = delayMessage;
    }

    public List<DelayMessage> getDelayMessage() {
        return delayMessage;
    }
}

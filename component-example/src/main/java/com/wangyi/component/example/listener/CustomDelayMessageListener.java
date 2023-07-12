package com.wangyi.component.example.listener;

import cn.hutool.core.io.FileUtil;
import com.wangyi.component.redisson.delay.DelayMessage;
import com.wangyi.component.redisson.delay.DelayMessageEvent;
import com.wangyi.component.redisson.delay.DelayMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CustomDelayMessageListener {

    /**
     * 消费延迟消息
     * 如果抛出异常, 则会根据最大重试次数, 重新将消息丢入延迟队列或私信队列
     * @param event
     * @return
     */
    @DelayMessageListener(value = "create_user", retry = 0, pullSize = 50, batchConsume = false)
    public void onCreateUser(DelayMessageEvent event) {
        log.info("消费 create_user 消息, message: {}", event.getDelayMessage());
        List<DelayMessage> messageList = event.getDelayMessage();
        List<String> list = new ArrayList<>();
        messageList.forEach(message -> {
            if (message.getMessage().contains("300")) {
                int i = 1 / 0;
            }
            long now = System.currentTimeMillis();
            long needExeTime = message.getTimeStamp() + message.getDelayTime() * 1000;
            long diffTime = now - needExeTime;
            if (diffTime > 3000) {
                String content = "diffTime: " + diffTime + " | " + message.toString();
                list.add(content);
            }
        });
        FileUtil.appendUtf8Lines(list, new File("C:\\Users\\xiaoqing\\Desktop\\delay.txt"));
    }

    /**
     * 消费延迟消息, 返回 true
     * @param event
     * @return
     */
    @DelayMessageListener(value = "create_org", retry = 2)
    public void onCreateOrg(DelayMessageEvent event) {
        log.info("消费 create_org 消息, message: {}", event.getDelayMessage());
        List<DelayMessage> messageList = event.getDelayMessage();
        messageList.forEach(delayMessage -> {
            if (delayMessage.getMessage().contains("3")) {
                int i = 1 / 0;
            }
        });
    }

}

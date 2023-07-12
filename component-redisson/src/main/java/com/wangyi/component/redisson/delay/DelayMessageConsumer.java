package com.wangyi.component.redisson.delay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DelayMessageConsumer implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ThreadPoolTaskExecutor delayMessageTaskExecutor;

    @Resource
    private DelayMessageUtil delayMessageUtil;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, DelayMessageListener> topicMap = listAllDelayTopic();
        log.info("容器启动完成, 开始监听延迟队列消息, topicMap:{}", topicMap.keySet());
        topicMap.forEach((topic, listener) -> {
            Thread listerThread = new Thread(() -> {
                while (true) {
                    try {
                        List<DelayMessage> messageList = delayMessageUtil.fetchMessageWithLua(topic, listener.pullSize());
                        if (CollectionUtils.isEmpty(messageList)) {
                            // 没取到消息, 休眠 1 秒后再取
                            Thread.sleep(1000);
                        }
                        if (listener.batchConsume()) {
                            // 如果是批量消费, 一个线程处理所有获取到的消息
                            delayMessageTaskExecutor.execute(new DelayMessageTask(applicationContext, messageList));
                        } else {
                            // 如果不是批量消费, 一个线程只处理一条消息
                            for (DelayMessage message : messageList) {
                                List<DelayMessage> list = new ArrayList<>(1);
                                list.add(message);
                                delayMessageTaskExecutor.execute(new DelayMessageTask(applicationContext, list));
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
            listerThread.setName("DelayMessageConsumer-" + topic);
            listerThread.start();
        });
    }

    /**
     * 获取所有的监听topic
     * @return
     */
    private Map<String, DelayMessageListener> listAllDelayTopic() {
        Map<String, DelayMessageListener> topicMap = new HashMap<>();
        List<Class<?>> classList = new ArrayList<>();
        Map<String, Object> componentList = applicationContext.getBeansWithAnnotation(Component.class);
        componentList.forEach((k, v) -> classList.add(applicationContext.getType(k)));
        for (Class<?> clazz : classList) {
            Method[] methods = clazz.getSuperclass().getDeclaredMethods();
            for (Method method : methods) {
                DelayMessageListener listener = method.getDeclaredAnnotation(DelayMessageListener.class);
                if (null != listener) {
                    String topic = listener.value();
                    if (StringUtils.hasText(topic)) {
                        topicMap.put(topic, listener);
                    }
                }
            }
        }
        return topicMap;
    }

}

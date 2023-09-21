package com.wangyi.component.redisson.delay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 延迟队列工具
 */
public class DelayMessageUtil {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String DELAY_BUSINESS_TOPIC_KEY = "delay:business:";
    public static final String DELAY_DEAD_TOPIC_KEY = "delay:dead:";
    public static final Long DELETE_SUCCESS = 1L;

    private DefaultRedisScript<List> redisScript;

    private StringRedisTemplate stringRedisTemplate;

    private ObjectMapper objectMapper;

    public DelayMessageUtil(ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate) {
        this.objectMapper = objectMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/delay_fetch_message.lua")));
        redisScript.setResultType(List.class);
    }

    /**
     * 发布延迟消息
     */
    public void publish(DelayMessage message) {
        try {
            String topic = DELAY_BUSINESS_TOPIC_KEY + message.getTopic();
            String data = objectMapper.writeValueAsString(message);
            long expireTime = message.getTimeStamp() + (message.getDelayTime() * 1000);
            stringRedisTemplate.opsForZSet().add(topic, data, expireTime);
            log.debug("发布延时消息成功, message: {}", data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取消息
     * @param topic 要获取数据的队列
     * @param size 每次取多少条数据
     * @return
     */
    List<DelayMessage> fetchMessage(String topic, int size) {
        try {
            // size 最大为 100
            size = Math.min(size, 100);
            topic = DELAY_BUSINESS_TOPIC_KEY + topic;
            List<DelayMessage> messageList = new ArrayList<>(size);
            // 从指定的 sortSet 中, 获取到期的消息, 每次取 5 条
            Set<String> sets = stringRedisTemplate.opsForZSet().rangeByScore(topic, 0, System.currentTimeMillis(), 0, size);
            if (CollectionUtils.isEmpty(sets)) {
                return messageList;
            }
            for (String val : sets) {
                // 删除成功，表示抢占到消息, 能保证多个节点消费消息, 只有一个节点能取到消息
                if (DELETE_SUCCESS.equals(stringRedisTemplate.opsForZSet().remove(topic, val))) {
                    log.debug("获取到延时消息: {}", val);
                    DelayMessage delayMessage = objectMapper.readValue(val, DelayMessage.class);
                    messageList.add(delayMessage);
                }
            }
            return messageList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过lua脚本获取延时消息
     * @param topic
     * @param size
     * @return
     */
    List<DelayMessage> fetchMessageWithLua(String topic, int size) {
        try {
            topic = DELAY_BUSINESS_TOPIC_KEY + topic;
            size = Math.min(size, 100);
            List<DelayMessage> messageList = new ArrayList<>(size);
            List<String> keys = Collections.singletonList(topic);
            List<String> args = new ArrayList<>(4);
            Collections.addAll(args, String.valueOf(0), String.valueOf(System.currentTimeMillis()), String.valueOf(0), String.valueOf(size));
            // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
            List<String> list = stringRedisTemplate.execute(redisScript, keys, args.toArray());
            if (CollectionUtils.isEmpty(list)) {
                return messageList;
            }
            for (String val : list) {
                log.debug("获取到延时消息: {}", val);
                DelayMessage delayMessage = objectMapper.readValue(val, DelayMessage.class);
                messageList.add(delayMessage);
            }
            return messageList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 重试, 最大重试3次
     * @param message
     * @param retry 最大重试次数
     */
    void retryPublish(DelayMessage message, int retry) {

        // 重试次数为0时, 丢入死信队列
        if (retry < 1) {
            pushDeadQueue(message);
            return;
        }

        // 如果小于最大重试次数, 重新将延迟消息丢入延迟队列中, 重新消费
        if (message.getRetry() < retry) {
            message.setRetry(message.getRetry() + 1);
            // 第一次重试, 在原来延迟时间上加 5 秒, 第二重试, 在原来延迟时间上加 10 秒, 第三次重试, 在原来的延迟时间上加 15 秒
            long delay = message.getRetry() * 5000L;
            long expireTime = message.getTimeStamp() + (message.getDelayTime() * 1000) + delay;
            publish(message, expireTime);
            return;
        }

        // 超过最大重试次数, 丢入死信队列
        pushDeadQueue(message);
    }

    /**
     * 发送延迟消息, 设置指定的过期时间
     * @param message
     * @param expireTime
     */
    private void publish(DelayMessage message, long expireTime) {
        try {
            String topic = DELAY_BUSINESS_TOPIC_KEY + message.getTopic();
            String data = objectMapper.writeValueAsString(message);
            stringRedisTemplate.opsForZSet().add(topic, data, expireTime);
            log.debug("发布延时消息成功, message: {}", data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将消费失败的消息丢入私信队列
     * @param message
     */
    private void pushDeadQueue(DelayMessage message) {
        try {
            String topic = DELAY_DEAD_TOPIC_KEY + message.getTopic();
            String data = objectMapper.writeValueAsString(message);
            long expireTime = message.getTimeStamp() + (message.getDelayTime() * 1000);
            stringRedisTemplate.opsForZSet().add(topic, data, expireTime);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}

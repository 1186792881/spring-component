package com.wangyi.component.redisson.delay;

import org.springframework.context.event.EventListener;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EventListener
public @interface DelayMessageListener {

    /**
     * 要监听的延迟队列 topic
     * @return
     */
    String value();

    /**
     * 消费失败重试次数
     * 0 表示不重试, 如果消费者消费失败, 直接将消费失败的消息丢入死信队列
     * 大于0时, 如果消费者消费失败, 重新将消息丢入到延迟队列中, 重新消费的次数大于该值, 则将消息丢入死信队列
     * 注意配置大于0时, 消费端必须保证消息的幂等
     * @return
     */
    int retry() default 0;

    /**
     * 是否批量消费
     * true- 每次处理拉取到的全部消息, 最大条数为 pullSize
     * false-每次只能处理一条消息
     * @return
     */
    boolean batchConsume() default false;

    /**
     * 每次拉取的消息数量
     * @return
     */
    int pullSize() default 10;

}

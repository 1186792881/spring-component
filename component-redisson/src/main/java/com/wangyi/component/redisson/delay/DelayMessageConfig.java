package com.wangyi.component.redisson.delay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class DelayMessageConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final int corePoolSize = Runtime.getRuntime().availableProcessors() + 1;
    private final int maxPoolSize = corePoolSize * 2;
    private static final int queueCapacity = 1000;
    private static final int keepAliveSeconds = 60;

    @Bean
    public DelayMessageUtil delayMessageUtil(ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate) {
        return new DelayMessageUtil(objectMapper, stringRedisTemplate);
    }

    @Bean(name = "delayMessageTaskExecutor")
    public ThreadPoolTaskExecutor delayMessageTaskExecutor(ObjectMapper objectMapper, DelayMessageUtil delayMessageUtil) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.setRejectedExecutionHandler(new DelayMessageRejectPolicy(delayMessageUtil));
        executor.initialize();
        return executor;
    }

    /**
     * 给子线程设置traceId
     */
    public static class MdcTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            Map<String, String> map = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (null != map) {
                        MDC.setContextMap(map);
                    }

                    String traceId = MDC.get("TRACE_ID");
                    if (!StringUtils.hasLength(traceId)) {
                        traceId = UUID.randomUUID().toString().replaceAll("-", "");
                        MDC.put("TRACE_ID", traceId);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        }
    }

    /**
     * 延迟消息, 线程池消费异常, 将消息重新推送到延迟队列中
     */
    public class DelayMessageRejectPolicy implements RejectedExecutionHandler {

        private DelayMessageUtil delayMessageUtil;

        public DelayMessageRejectPolicy(DelayMessageUtil delayMessageUtil) {
            this.delayMessageUtil = delayMessageUtil;
        }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof DelayMessageTask) {
                try {
                    DelayMessageTask task = (DelayMessageTask) r;
                    List<DelayMessage> messageList = task.getDelayMessage();
                    log.error("延迟队列线程池消费失败, message: {}", messageList);
                    if (!CollectionUtils.isEmpty(messageList)) {
                        messageList.forEach(msg -> {
                            try {
                                delayMessageUtil.retryPublish(msg, 0);
                            } catch (Exception e) {
                                log.error("延迟消息重试失败, message: {}", msg, e);
                            }
                        });
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}


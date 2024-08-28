package com.wangyi.component.web.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.wangyi.component.base.constant.BaseConstant;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class ThreadPoolConfig {

    private final int corePoolSize = Runtime.getRuntime().availableProcessors() + 1;
    private final int maxPoolSize = corePoolSize * 2;
    private static final int queueCapacity = 1000;
    private static final int keepAliveSeconds = 60;

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();


        //return TtlExecutors.getTtlExecutor(executor);

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

                    String traceId = MDC.get(BaseConstant.TRACE_ID);
                    if (!StringUtils.hasLength(traceId)) {
                        traceId = UUID.randomUUID().toString().replaceAll("-", "");
                        MDC.put(BaseConstant.TRACE_ID, traceId);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        }
    }

}

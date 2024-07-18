package com.wangyi.component.redisson.ratelimit.window;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitUtil {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String REDIS_LIMIT_KEY_PREFIX = "ratelimit:";

    /**
     * 限流脚本
     */
    private DefaultRedisScript<Long> limitRedisScript;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public RateLimitUtil() {
        this.limitRedisScript = new DefaultRedisScript<>();
        limitRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/slider_window_ratelimit.lua")));
        limitRedisScript.setResultType(Long.class);
    }

    /**
     * 检测是否需要限流
     * @param key 限流key
     * @param max 最大次数, 在 window 时间窗口内, 访问次数大于 max 时, 则被限流
     * @param window 需要统计的时间窗口
     * @param timeUnit window 参数的单位
     * 例子: max = 10, window = 1, timeUnit = MINUTES, 在 1 分钟内, 访问次数大于 10, 就会被限流
     * @return
     */
    public boolean shouldLimited(String key, long max, long window, TimeUnit timeUnit) {
        // 最终的 key 格式为：
        // limit:自定义key:IP
        // limit:类名.方法名:IP
        key = REDIS_LIMIT_KEY_PREFIX + key;
        // 统一使用单位毫秒
        long ttl = timeUnit.toMillis(window);
        // 当前时间毫秒数
        long now = Instant.now().toEpochMilli();
        long expired = now - ttl;
        // 注意这里必须转为 String,否则会报错 java.lang.Long cannot be cast to java.lang.String
        Long executeTimes = stringRedisTemplate.execute(limitRedisScript, Collections.singletonList(key), String.valueOf(now), String.valueOf(ttl), String.valueOf(expired), String.valueOf(max));
        if (executeTimes != null) {
            if (executeTimes == 0) {
                log.error("[{}] 在单位时间 {} 毫秒内已达到访问上限，当前接口上限 {}", key, ttl, max);
                return true;
            } else {
                log.debug("[{}] 在单位时间 {} 毫秒内访问 {} 次", key, ttl, executeTimes);
                return false;
            }
        }
        return false;
    }

}

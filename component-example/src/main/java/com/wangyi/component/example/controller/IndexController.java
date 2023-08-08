package com.wangyi.component.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangyi.component.example.client.BingClient;
import com.wangyi.component.example.repository.mysql.entity.BumUser;
import com.wangyi.component.example.service.IndexService;
import com.wangyi.component.redisson.delay.DelayMessage;
import com.wangyi.component.redisson.delay.DelayMessageUtil;
import com.wangyi.component.redisson.lock.DistributedLockUtil;
import com.wangyi.component.redisson.ratelimit.RateLimiter;
import com.wangyi.component.web.annotation.LogExclude;
import com.wangyi.component.web.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@Validated
public class IndexController {

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private DistributedLockUtil distributedLockUtil;

    @Resource
    private IndexService indexService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private DelayMessageUtil delayMessageUtil;

    @Resource
    private BingClient bingClient;

    @GetMapping("/index")
    public Result<String> index(@RequestParam String name) {
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                log.info("线程池, name:" + name);
            }
        });

        indexService.async();
        return Result.success("hello " + name);
    }

    @PostMapping("/index1")
    public Result<String> index1(@RequestParam String name) {
        return distributedLockUtil.tryLock(name, -1, TimeUnit.SECONDS, () -> {
            return Result.success("hello " + name);
        });
    }

    @DeleteMapping("/index2")
    public Result<String> index2(@RequestParam String name) {
        String ret = indexService.getUser(name);
        return Result.success(ret);
    }

    @PutMapping("/index3")
    public Result<Object> index3(@RequestParam String name) {
        Map<String, Object> ret = indexService.getOrg(name);
        return Result.success(ret);
    }

    @RequestMapping("/index4")
    public Result<Object> index4(@RequestParam String name) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", name);
        map.put("date", new Date());
        redisTemplate.opsForValue().set(name, map);

        stringRedisTemplate.opsForValue().set(name + "aa", objectMapper.writeValueAsString(map));
        return Result.success(map);
    }

    @GetMapping("/get")
    public Result<String> get(@NotBlank(message = "name不能为空") String name, @NotBlank(message = "id不能为空") String id) {
        return Result.success("hello" + name);
    }

    @GetMapping("/getUser")
    @LogExclude
    public Result<Object> getUser(Long id) {
        return Result.success(indexService.getUser(id));
    }

    @GetMapping("/getUser2")
    public Result<Object> getUser2(Long id) {
        BumUser user = (BumUser) redisTemplate.opsForValue().get("example:bum_user::7033048159756292");
        return Result.success(user);
    }

    @RateLimiter(max = 5)
    @GetMapping("/getUser3")
    public Result<Object> getUser3(Long id) {
        RBucket<BumUser> a = redissonClient.getBucket("example:bum_user::7033048159756292");
        BumUser user = a.get();
        return Result.success(user);
    }

    @GetMapping("/publishCreateUser")
    public Result<Void> publishCreateUser(Integer count, Integer time) {
        for (int i = 1; i <= count; i++) {
            DelayMessage msg = new DelayMessage("create_user", String.valueOf(i), time);
            delayMessageUtil.publish(msg);
        }
        return Result.success();
    }

    @GetMapping("/publishCreateOrg")
    public Result<Void> publishCreateOrg(String name) {
        for (int i = 1; i <= 10; i++) {
            delayMessageUtil.publish(new DelayMessage("create_org",name + i, 10));
        }
        return Result.success();
    }

    @GetMapping("/bing")
    public Result<String> bing(String q) {
        String index = bingClient.search(q);
        return Result.success(index);
    }


}

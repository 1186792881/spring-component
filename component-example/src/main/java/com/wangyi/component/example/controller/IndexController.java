package com.wangyi.component.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangyi.component.base.exception.BizAssert;
import com.wangyi.component.base.vo.Result;
import com.wangyi.component.encrypt.api.annotation.DecryptRequestBody;
import com.wangyi.component.encrypt.api.annotation.EncryptResponseBody;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.example.client.BingClient;
import com.wangyi.component.example.constant.ExampleResultCode;
import com.wangyi.component.example.repository.mysql.entity.User;
import com.wangyi.component.example.service.IndexService;
import com.wangyi.component.example.vo.ReqPublishCreateUserVO;
import com.wangyi.component.redisson.delay.DelayMessage;
import com.wangyi.component.redisson.delay.DelayMessageUtil;
import com.wangyi.component.redisson.lock.DistributedLockUtil;
import com.wangyi.component.redisson.ratelimit.bucket.BucketRateLimiter;
import com.wangyi.component.redisson.ratelimit.window.WindowRateLimiter;
import com.wangyi.component.uid.core.impl.CachedUidGenerator;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@Validated
@EncryptResponseBody(encryptType = EncryptType.AES)
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
        return distributedLockUtil.tryLock(name, () -> {
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
        BizAssert.isFalse("aa".equals(name), ExampleResultCode.PARAM_INVALID, "name: " + name);
        return Result.success("hello" + name);
    }

    @GetMapping("/getUser2")
    public Result<Object> getUser2(Long id) {
        User user = (User) redisTemplate.opsForValue().get("example:bum_user::7033048159756292");
        return Result.success(user);
    }

    @WindowRateLimiter(max = 5)
    @GetMapping("/getUser3")
    public Result<Object> getUser3() {
        RBucket<User> a = redissonClient.getBucket("example:bum_user::7033048159756292");
        User user = a.get();
        return Result.success(user);
    }

    @BucketRateLimiter(key = "getUser4")
    @GetMapping("/getUser4")
    public Result<Object> getUser4() {
        RBucket<User> a = redissonClient.getBucket("example:bum_user::7033048159756292");
        User user = a.get();
        return Result.success(user);
    }

    @DecryptRequestBody(encryptType = EncryptType.AES)
    @PostMapping("/publishCreateUser")
    public Result<Void> publishCreateUser(@RequestBody ReqPublishCreateUserVO req) {
        for (int i = 1; i <= req.getCount(); i++) {
            DelayMessage msg = new DelayMessage("create_user", String.valueOf(i), req.getTime());
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

    @Resource
    private CachedUidGenerator cachedUidGenerator;

    @GetMapping("/generateId")
    public Result<List<Long>> generateId(Integer count) {
        List<Long> idList = new ArrayList<>();
        for (int i=0; i<count; i++) {
            Long id = cachedUidGenerator.getUID();
            if (idList.contains(id)) {
                throw new RuntimeException("id重复");
            }
            idList.add(id);
        }
        return Result.success(idList);
    }

    @EncryptResponseBody(encryptType = EncryptType.DES)
    @GetMapping("/parseId")
    public Result<String> parseId(Long id) {
        return  Result.success(cachedUidGenerator.parseUID(id));
    }

    @PostMapping("/user/save")
    public Result<Void> saveUser(@RequestBody User user) {
        indexService.saveUser(user);
        return Result.success();
    }

    @GetMapping("/user/list")
    public Result<List<User>> listUser(String searchKey) {
        return Result.success(indexService.listUser(searchKey));
    }

    @PostMapping("/user/update")
    public Result<Void> updateUser(@RequestBody User user) {
        indexService.updateUser(user);
        return Result.success();
    }

}

package com.wangyi.component.example.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wangyi.component.example.repository.mysql.dao.BumUserDao;
import com.wangyi.component.example.repository.mysql.entity.BumUser;
import com.wangyi.component.redisson.lock.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class IndexService {

    @Resource
    private BumUserDao bumUserDao;

    @DistributedLock(key = "#id")
    public String getUser(String id) {
        return "hello" + id;
    }

    @Cacheable(cacheNames = "org", key = "#id")
    public Map<String, Object> getOrg(String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("date", new Date());
        return map;
    }

    @CacheEvict(cacheNames = "org", key = "#id")
    public void deleteOrg(String id) {

    }

    @Cacheable(cacheNames = "bum_user", key = "#id")
    public BumUser getUser(Long id) {
        IdWorker.getId();
        return bumUserDao.getById(id);
    }

    @Async("threadPoolTaskExecutor")
    public void async() {
        log.info("执行async方法");
    }

    public BumUser getUserByPhone(String phone) {
        return bumUserDao.lambdaQuery()
                .select(BumUser::getPhone, BumUser::getEmail)
                .ge(BumUser::getPhone, phone)
                .one();
    }


}

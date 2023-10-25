package com.wangyi.component.example.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wangyi.component.encrypt.data.annotation.EncryptField;
import com.wangyi.component.encrypt.data.enums.EncryptType;
import com.wangyi.component.example.repository.mysql.dao.UserDao;
import com.wangyi.component.example.repository.mysql.entity.User;
import com.wangyi.component.redisson.lock.DistributedLock;
import com.wangyi.component.uid.core.impl.CachedUidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IndexService {

    @Resource
    private UserDao userDao;

    @Resource
    private CachedUidGenerator cachedUidGenerator;

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
    public User getUser(Long id) {
        IdWorker.getId();
        return userDao.getById(id);
    }

    @Async("threadPoolTaskExecutor")
    public void async() {
        log.info("执行async方法");
    }

    public User getUserByPhone(String phone) {
        return userDao.lambdaQuery()
                .select(User::getPhone, User::getUsername)
                .ge(User::getPhone, phone)
                .one();
    }


    public void saveUser(User user) {
        user.setId(cachedUidGenerator.getUID());
        userDao.save(user);
    }

    public List<User> listUser(String searchKey) {
        if (StrUtil.isNotBlank(searchKey)) {
            List a = userDao.lambdaQuery()
                    .eq(User::getPassword, searchKey)
                    .or()
                    .eq(User::getIdNumber, searchKey)
                    .list();
            log.info(a.toString());
            List b = userDao.listUser(searchKey);
            log.info(b.toString());
        }

        return userDao.list();
    }

    public void updateUser(User user) {
        userDao.lambdaUpdate()
                .set(User::getUsername, user.getUsername())
                .set(User::getPassword, user.getPassword())
                .set(User::getIdNumber, user.getIdNumber())
                .set(User::getPhone, user.getPhone())
                .eq(User::getId, user.getId())
                .update();
    }
}

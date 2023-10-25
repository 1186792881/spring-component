package com.wangyi.component.example.repository.mysql.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangyi.component.example.repository.mysql.entity.User;
import com.wangyi.component.example.repository.mysql.dao.UserDao;
import com.wangyi.component.example.repository.mysql.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author xiaoqing
* @description 针对表【bum_user(用户表)】的数据库操作Service实现
* @createDate 2023-07-03 11:09:41
*/
@Service
public class UserDaoImpl extends ServiceImpl<UserMapper, User>
    implements UserDao {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<User> listUser(String searchKey) {
        return userMapper.listUser(searchKey, searchKey);
    }
}





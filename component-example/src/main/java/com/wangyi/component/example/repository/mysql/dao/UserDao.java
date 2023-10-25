package com.wangyi.component.example.repository.mysql.dao;

import com.wangyi.component.example.repository.mysql.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author xiaoqing
* @description 针对表【bum_user(用户表)】的数据库操作Service
* @createDate 2023-07-03 11:09:41
*/
public interface UserDao extends IService<User> {

    List<User> listUser(String searchKey);
}

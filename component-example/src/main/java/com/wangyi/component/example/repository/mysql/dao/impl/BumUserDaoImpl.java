package com.wangyi.component.example.repository.mysql.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangyi.component.example.repository.mysql.entity.BumUser;
import com.wangyi.component.example.repository.mysql.dao.BumUserDao;
import com.wangyi.component.example.repository.mysql.mapper.BumUserMapper;
import org.springframework.stereotype.Service;

/**
* @author xiaoqing
* @description 针对表【bum_user(用户表)】的数据库操作Service实现
* @createDate 2023-07-03 11:09:41
*/
@Service
public class BumUserDaoImpl extends ServiceImpl<BumUserMapper, BumUser>
    implements BumUserDao {

}





package com.wangyi.component.example.repository.mysql.mapper;

import com.wangyi.component.encrypt.data.annotation.EncryptField;
import com.wangyi.component.encrypt.data.enums.EncryptType;
import com.wangyi.component.example.repository.mysql.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author xiaoqing
* @description 针对表【bum_user(用户表)】的数据库操作Mapper
* @createDate 2023-07-03 11:09:41
* @Entity com.wangyi.component.example.repository.mysql.entity.BumUser
*/
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where password = #{searchPassword} or id_number = #{searchIdNumber}")
    List<User> listUser(@EncryptField(encryptType = EncryptType.AES) @Param("searchPassword") String searchPassword,
                        @EncryptField(encryptType = EncryptType.DES) @Param("searchIdNumber") String searchIdNumber);
}





package com.wangyi.component.example.repository.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.wangyi.component.encrypt.data.annotation.EncryptField;
import com.wangyi.component.encrypt.data.enums.EncryptType;
import lombok.Data;

/**
 * 用户表
 * @TableName bum_user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    @EncryptField(encryptType = EncryptType.AES)
    private String password;

    /**
     * 身份证
     */
    @TableField
    @EncryptField(encryptType = EncryptType.DES)
    private String idNumber;

    /**
     * 电话
     */
    private String phone;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
package com.wangyi.component.example.repository.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName bum_user
 */
@TableName(value ="bum_user")
@Data
public class BumUser implements Serializable {
    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 分类ID
     */
    private Long classificationId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 状态 ENABLE-启用, DISABLE-禁用, LOCK-锁定, EXPIRE-过期
     */
    private String status;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 性别 M-男, F-女
     */
    private String gender;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 头像图片url地址
     */
    private String avatarUrl;

    /**
     * 直属主管id
     */
    private Long supervisorId;

    /**
     * 用户UUID
     */
    private String uuid;

    /**
     * 用户来源
     */
    private String source;

    /**
     * 
     */
    private Object attributes;

    /**
     * 
     */
    private Object scimAttributes;

    /**
     * 是否删除, YES-是，NO-否
     */
    private String isDeleted;

    /**
     * 创建人ID
     */
    private Long creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 密码状态, SYSTEM_GENERATE:系统生成, USER_INPUT:用户录入
     */
    private String passwordStatus;

    /**
     * 上次登陆时间
     */
    private Date lastLoginTime;

    /**
     * 上次修改密码时间
     */
    private Date lastPwdChangeTime;

    /**
     * 是否为租户管理员
     */
    private String isTenantAdmin;

    /**
     * 锁定状态 PERMANENT-永久锁定, TEMPORARY-临时锁定
     */
    private String lockStatus;

    /**
     * 隐私协议更新时间
     */
    private Date privacyAgreementConfirmTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
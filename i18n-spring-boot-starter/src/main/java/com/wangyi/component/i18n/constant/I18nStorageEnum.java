package com.wangyi.component.i18n.constant;

/**
 * 国际化存储模式
 */
public enum I18nStorageEnum {

    /**
     * 在 redis 保存国际化信息
     */
    redis,

    /**
     * 在 properties 保存国际化信息
     */
    properties,

    /**
     * 在 nacos 保存国际化信息
     */
    nacos,

    /**
     * 在 数据库 保存国际化信息
     */
    db;
}

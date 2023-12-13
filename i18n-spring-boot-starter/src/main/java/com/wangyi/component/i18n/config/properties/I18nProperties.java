package com.wangyi.component.i18n.config.properties;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.wangyi.component.i18n.config.bean.Database;
import com.wangyi.component.i18n.config.bean.Nacos;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("i18n")
@Component
@Data
public class I18nProperties {

    /**
     * 是否启用国际化组件
     */
    private boolean enable = true;

    /**
     * 国际化信息保存方式 (properties(默认), redis, nacos, database)
     */
    private String storage = "properties";

    /**
     * 存储前缀
     * redis:               i18n:result_code:zh-CN
     * properties:          i18n/result_code_zh-CN.properties
     * nacos:               i18n_result_code_zh-CN.properties
     * database:            表名: i18n
     */
    private String i18nStoragePrefix = "i18n";

    /**
     * 国际化, 本地缓存失效时间 单位毫秒
     */
    private Long localCacheTimeOut = 3600 * 1000L;

    /**
     * 本地缓存国际化最大个数
     */
    private Integer localCacheCapacity = 2000;

    /**
     * 是否在服务启动时, 扫描国际化信息
     * 可以将代码中国际化枚举保存到 redis 或 database
     */
    private boolean enableScan = true;

    /**
     * 要扫描的包, 当 enableScan = true 时生效
     * 配置要扫描的包路径 例如: com.wangyi
     */
    private String scanPackage;

    /**
     * nacos 相关配置
     */
    private Nacos nacos;

    /**
     * database 相关配置 (可以配置额外的数据源存储国际化信息)
     * 如果 storage = database, 且 database 不进行配置时, 采用服务的默认数据源
     */
    private Database database;

    /**
     * 生成缓存key
     * @param type
     * @param language
     * @param code
     * @return
     */
    public String getCacheKey(String type, String language, String code) {
        return StrUtil.join(StrPool.COLON, i18nStoragePrefix, type, language, code);
    }

}

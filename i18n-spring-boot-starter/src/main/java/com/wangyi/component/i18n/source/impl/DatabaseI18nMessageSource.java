package com.wangyi.component.i18n.source.impl;

import cn.hutool.cache.impl.LFUCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;
import com.wangyi.component.i18n.config.bean.Database;
import com.wangyi.component.i18n.config.properties.I18nProperties;
import com.wangyi.component.i18n.source.I18nMessageSource;
import com.wangyi.component.i18n.source.entity.I18n;
import com.wangyi.component.i18n.util.ScanUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ConditionalOnProperty(value = "i18n.storage", havingValue = "database")
@Configuration
@Lazy
@Slf4j
public class DatabaseI18nMessageSource implements I18nMessageSource {

    private static final String EXIST_SQL = "select count(*) from i18n where type = ? and language = ? and code = ?";
    private static final String VALUE_SQL = "select code, value from i18n where type = :type and language = :language and code in (:code)";

    private final ObjectProvider<DataSource> dataSourceObjectProvider;
    private final I18nProperties i18nProperties;
    private final DataSource dataSource;
    // key: i18n:result_code:zh-CN:example.00001
    private final LFUCache<String, String> localCache;

    public DatabaseI18nMessageSource(I18nProperties i18nProperties, ObjectProvider<DataSource> dataSourceObjectProvider) {
        this.dataSourceObjectProvider = dataSourceObjectProvider;
        this.i18nProperties = i18nProperties;
        this.dataSource = initDataSource();
        this.localCache = new LFUCache<>(i18nProperties.getLocalCacheCapacity());
    }

    @Override
    public String getMessage(String type, String language, String code) {
        if (StrUtil.hasBlank(type, code, language)) {
            return null;
        }

        String localCacheKey = i18nProperties.getCacheKey(type, language, code);
        String value = localCache.get(localCacheKey, false);
        if (null == value) {
            Map<String, String> msgMap = getMessage(type, language, CollUtil.newArrayList(code));
            value = msgMap.get(code);
            if (null != value) {
                localCache.put(localCacheKey, value, i18nProperties.getLocalCacheTimeOut());
            }
        }
        return value;
    }

    @SneakyThrows
    @Override
    public Map<String, String> getMessage(String type, String language, List<String> codeList) {
        if (StrUtil.hasBlank(type, language) || CollUtil.isEmpty(codeList)) {
            return Collections.emptyMap();
        }

        List<Entity> valueList = Db.use(dataSource).query(VALUE_SQL,
                Entity.of().set(LambdaUtil.getFieldName(I18n::getType), type)
                        .set(LambdaUtil.getFieldName(I18n::getLanguage), language)
                        .set(LambdaUtil.getFieldName(I18n::getCode), codeList.toArray())
        );

        return valueList.stream()
                .collect(Collectors.toMap(
                        t -> t.getStr(LambdaUtil.getFieldName(I18n::getCode)),
                        t -> t.getStr(LambdaUtil.getFieldName(I18n::getValue))));
    }

    @SneakyThrows
    @Override
    public void initMessage() {
        // key->i18n:result_code:zh-CN, value->{code, msg}
        List<I18n> i18nList = ScanUtil.scanI18nEnum(i18nProperties);
        if (CollUtil.isEmpty(i18nList)) {
            return;
        }

        // 筛选出在 i18n 表中不存在的国际化信息
        i18nList = i18nList.stream().filter(i18n -> {
            try {
                BigDecimal count = (BigDecimal) Db.use(dataSource).queryNumber(EXIST_SQL, i18n.getType(), i18n.getLanguage(), i18n.getCode());
                return NumberUtil.equals(count, new BigDecimal("0"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        if (CollUtil.isEmpty(i18nList)) {
            return;
        }

        // 批量保存国际化信息
        List<Entity> entityList = i18nList.stream()
                .map(i18n -> Entity.create()
                        .setTableName(I18n.TABLE_NAME)
                        .set(LambdaUtil.getFieldName(I18n::getId), IdUtil.getSnowflakeNextId())
                        .set(LambdaUtil.getFieldName(I18n::getType), i18n.getType())
                        .set(LambdaUtil.getFieldName(I18n::getLanguage), i18n.getLanguage())
                        .set(LambdaUtil.getFieldName(I18n::getCode), i18n.getCode())
                        .set(LambdaUtil.getFieldName(I18n::getValue), i18n.getValue())
                        .set(I18n.CREATE_TIME, LocalDateTime.now())
                        .set(I18n.UPDATE_TIME, LocalDateTime.now())
                )
                .collect(Collectors.toList());
        Db.use(dataSource).insert(entityList);
        log.info("扫描国际化枚举到数据库 {} 条", entityList.size());

    }

    /**
     * 初始化数据源
     *
     * @return
     */
    private DataSource initDataSource() {

        // 单独配置的国际化数据源
        Database database = i18nProperties.getDatabase();
        if (null != database) {
            String driverClassName = database.getDriverClassName();
            String url = database.getUrl();
            String username = database.getUsername();
            String password = database.getPassword();

            if (StrUtil.isNotBlank(driverClassName) &&
                    StrUtil.isNotBlank(url) &&
                    StrUtil.isNotBlank(username) &&
                    StrUtil.isNotBlank(password)) {
                Setting setting = new Setting();
                setting.set("driverClassName", driverClassName);
                setting.set("jdbcUrl", url);
                setting.set("username", username);
                setting.set("password", password);
                return DSFactory.create(setting).getDataSource();
            }
        }

        // 系统默认的数据源
        DataSource ds = dataSourceObjectProvider.getIfAvailable();
        if (null == ds) {
            throw new ExceptionInInitializerError("国际化 database 数据源不存在");
        }
        return ds;

    }

}

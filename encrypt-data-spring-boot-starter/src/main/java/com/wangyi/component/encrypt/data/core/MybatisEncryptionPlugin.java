package com.wangyi.component.encrypt.data.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.wangyi.component.encrypt.data.annotation.EncryptField;
import com.wangyi.component.encrypt.data.config.EncryptDataProperties;
import com.wangyi.component.encrypt.data.handler.EncryptBody;
import com.wangyi.component.encrypt.data.handler.EncryptHandler;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * 加密插件, 处理 insert, update, delete, query时的加密
 * 拦截 Executor 的 update, query方法
 * 支持查询的方式：
 *  1. 原生mybatis mapper 查询，入参为单一对象的方法： User query(xxDTO dto)  入参对象加密字段中包含 @EncryptField 注解
 *  2. 原生mybatis mapper 查询，入参为字符串： User queryByPhone(@EncryptField @Param("phone") String phone)
 *  3. mybatis-plus单表查询：xxService.lambdaQuery().xx 、xxService.lambdaUpdate().xx
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class MybatisEncryptionPlugin implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(MybatisEncryptionPlugin.class);

    private final boolean failFast;
    private final boolean keepParameter;

    public MybatisEncryptionPlugin(EncryptDataProperties encryptDataProperties) {
        this.failFast = encryptDataProperties.isFailFast();
        this.keepParameter = encryptDataProperties.isKeepParameter();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        if (Util.encryptionRequired(parameter, ms.getSqlCommandType())) {
            Object copyParameter = new Object();
            if (keepParameter) {
                BeanUtil.copyProperties(parameter, copyParameter);
            }
            doEncrypt(parameter, ms);
            Object result = invocation.proceed();
            if (keepParameter) {
                // doDecrypt(parameter, ms);
                BeanUtil.copyProperties(copyParameter, parameter);
            }
            return result;
        } else {
            return invocation.proceed();
        }
    }

    private void doEncrypt(Object parameter, MappedStatement mappedStatement) {
        processParameter(Mode.ENCRYPT, parameter, mappedStatement);
    }

    private void doDecrypt(Object parameter, MappedStatement mappedStatement) {
        processParameter(Mode.DECRYPT, parameter, mappedStatement);
    }

    private void processParameter(Mode mode, Object parameter, MappedStatement mappedStatement) {
        boolean isParamMap = parameter instanceof MapperMethod.ParamMap;
        if (isParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) parameter;
            if (paramMap.containsKey("ew")) {
                // 处理mybatis-plus单表查询 xxService.lambdaQuery().xx,  xxService.lambdaUpdate().xx
                processMybatisPlusParamMap(mappedStatement, parameter);
            } else {
                // 原生mybatis mapper 查询，入参为字符串： User queryByPhone(@EncryptField @Param("phone") String phone)
                processParamMap(mode, paramMap, mappedStatement);
            }
        } else {
            // 原生mybatis mapper 查询，入参为单一对象的方法： User query(xxDTO dto)  入参对象加密字段中包含 @EncryptField 注解
            processEntity(mode, parameter);
        }
    }

    private void processMybatisPlusParamMap(MappedStatement mappedStatement, Object parameter) {
        Set<Field> encryptFields = Util.getEncryptField(mappedStatement);
        if (CollUtil.isEmpty(encryptFields)) {
            return;
        }
        MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) parameter;
        AbstractWrapper ew = (AbstractWrapper) paramMap.get("ew");
        if (null != ew) {
            Map<String, Object> paramNameValuePairs = ew.getParamNameValuePairs();
            Map<String, Util.ColumMapping> columMappingMap = Util.parseParamAliasNameMap(ew, paramNameValuePairs);
            for (Field field : encryptFields) {
                String columnName = field.getName();
                TableField tableField = field.getAnnotation(TableField.class);
                EncryptField encryptField = field.getAnnotation(EncryptField.class);
                if (null != tableField) {
                    String tableFieldValue = tableField.value();
                    columnName = StrUtil.isBlank(tableFieldValue) ? StrUtil.toUnderlineCase(columnName) : tableFieldValue;
                }
                Util.ColumMapping columMapping = columMappingMap.get(columnName);
                if (columMapping != null) {
                    EncryptHandler encryptHandler = Util.getEncryptHandler(encryptField.encryptType());
                    EncryptBody encryptBody = new EncryptBody();
                    encryptBody.setBody(columMapping.getVal());
                    encryptBody.setEncryptType(encryptField.encryptType());
                    encryptBody.setEncryptKeyType(encryptField.encryptKeyType());
                    String cipher = encryptHandler.encrypt(encryptBody);
                    paramNameValuePairs.put(columMapping.getAliasName(), cipher);
                }
            }
        }
    }

    private void processParamMap(Mode mode, MapperMethod.ParamMap paramMap, MappedStatement mappedStatement) {
        Map<String, EncryptField> encryptFieldMap = Util.gentEncryptField(mappedStatement);
        if (encryptFieldMap == null || encryptFieldMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, EncryptField> entry : encryptFieldMap.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = paramMap.get(paramName);
            if (paramValue == null) {
                continue;
            }
            EncryptField encryptField = entry.getValue();
            if (paramValue instanceof Collection) {
                Collection<?> list = (Collection<?>) paramValue;
                if (list.isEmpty()) {
                    continue;
                }
                Object nonNullItem = list.stream().filter(Objects::nonNull).findFirst().orElse(null);
                if (nonNullItem == null) {
                    continue;
                }
                if (nonNullItem instanceof String) {
                    //noinspection rawtypes
                    Collection newList = new ArrayList();
                    for (Object item : list) {
                        newList.add(processString(mode, item, encryptField));
                    }
                    // Replace plain text with ciphertext
                    list.clear();
                    list.addAll(newList);
                } else {
                    Class<?> itemClass = nonNullItem.getClass();
                    Set<Field> encryptedFields = Util.getEncryptField(itemClass);
                    if (encryptedFields != null && !encryptedFields.isEmpty()) {
                        for (Object item : list) {
                            processFields(mode, encryptedFields, item);
                        }
                    }
                }
            } else if (paramValue instanceof String) {
                paramMap.put(paramName, processString(mode, paramValue, encryptField));
            } else {
                processFields(mode, Util.getEncryptField(paramValue.getClass()), paramValue);
            }
        }
    }

    private <T> void processEntity(Mode mode, T parameter) throws MybatisCryptoException {
        Set<Field> encryptedFields = Util.getEncryptField(parameter.getClass());
        if (encryptedFields == null || encryptedFields.isEmpty()) {
            return;
        }
        processFields(mode, encryptedFields, parameter);
    }

    private Object processString(Mode mode, Object originalValue, EncryptField encryptField) {
        try {
            EncryptHandler encryptHandler = Util.getEncryptHandler(encryptField.encryptType());
            EncryptBody encryptBody = new EncryptBody();
            encryptBody.setBody(originalValue.toString());
            encryptBody.setEncryptType(encryptField.encryptType());
            encryptBody.setEncryptKeyType(encryptField.encryptKeyType());
            return Util.doFinal(encryptHandler, mode, encryptBody);
        } catch (Exception e) {
            if (failFast) {
                throw new MybatisCryptoException(e);
            } else {
                log.warn("process encrypted parameter error.", e);
                return originalValue;
            }
        }
    }

    private void processFields(Mode mode, Set<Field> encryptedFields, Object entry) throws MybatisCryptoException {
        if (encryptedFields == null || encryptedFields.isEmpty()) {
            return;
        }
        for (Field field : encryptedFields) {
            try {
                EncryptField encryptedField = field.getAnnotation(EncryptField.class);
                if (encryptedField == null) {
                    continue;
                }
                if (!String.class.equals(field.getType())) {
                    continue;
                }
                Object originalVal = field.get(entry);
                if (originalVal == null) {
                    continue;
                }
                if (((String) originalVal).isEmpty()) {
                    continue;
                }
                EncryptHandler encryptHandler = Util.getEncryptHandler(encryptedField.encryptType());
                EncryptBody encryptBody = new EncryptBody();
                encryptBody.setBody(originalVal.toString());
                encryptBody.setEncryptType(encryptedField.encryptType());
                encryptBody.setEncryptKeyType(encryptedField.encryptKeyType());
                String updatedVal = Util.doFinal(encryptHandler, mode, encryptBody);
                field.set(entry, updatedVal);
            } catch (Exception e) {
                if (failFast) {
                    throw new MybatisCryptoException(e);
                } else {
                    log.warn("process encrypted filed error.", e);
                }
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

}

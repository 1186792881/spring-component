package com.wangyi.component.encrypt.data.core;

import com.wangyi.component.encrypt.data.annotation.EncryptField;
import com.wangyi.component.encrypt.data.config.EncryptDataProperties;
import com.wangyi.component.encrypt.data.handler.EncryptBody;
import com.wangyi.component.encrypt.data.handler.EncryptHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * 解密插件, 处理 select 结果的解密
 * 拦截 ResultSetHandler 的 handleResultSets 方法
 */
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class MybatisDecryptionPlugin implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(MybatisDecryptionPlugin.class);

    private final boolean failFast;

    public MybatisDecryptionPlugin(EncryptDataProperties encryptDataProperties) {
        this.failFast = encryptDataProperties.isFailFast();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        try {
            decryptObj(result);
        } catch (Exception e) {
            if (failFast) {
                throw new MybatisCryptoException(e);
            } else {
                log.warn("decrypt filed error.", e);
            }
        }
        return result;
    }

    private void decryptObj(Object obj) throws Exception {
        if (obj == null) {
            return;
        }
        if (obj instanceof Collection) {
            Collection<?> list = (Collection<?>) obj;
            if (list.isEmpty()) {
                return;
            }
            Object firstNonNullItem = list.stream().filter(Objects::nonNull).findFirst().orElse(null);
            if (!Util.decryptionRequired(firstNonNullItem)) {
                return;
            }
            Set<Field> encryptedFields = Util.getEncryptField(firstNonNullItem.getClass());
            if (encryptedFields == null || encryptedFields.isEmpty()) {
                return;
            }
            for (Object item : list) {
                decryptObj(item);
            }
        } else {
            if (Util.decryptionRequired(obj)) {
                Set<Field> encryptedFields = Util.getEncryptField(obj.getClass());
                if (encryptedFields == null || encryptedFields.isEmpty()) {
                    return;
                }
                for (Field field : encryptedFields) {
                    decryptField(field, obj);
                }
            }
        }
    }

    private void decryptField(Field field, Object obj) throws Exception {
        EncryptField encryptedField = field.getAnnotation(EncryptField.class);
        if (encryptedField == null) {
            return;
        }
        Object cipher = field.get(obj);
        if (cipher == null) {
            return;
        }
        if (cipher instanceof String) {
            EncryptHandler encryptHandler = Util.getEncryptHandler(encryptedField.encryptType());
            EncryptBody encryptBody = new EncryptBody();
            encryptBody.setBody(cipher.toString());
            encryptBody.setEncryptType(encryptedField.encryptType());
            encryptBody.setEncryptKeyType(encryptedField.encryptKeyType());
            String plain = encryptHandler.decrypt(encryptBody);
            field.set(obj, plain);
        } else {
            decryptObj(cipher);
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

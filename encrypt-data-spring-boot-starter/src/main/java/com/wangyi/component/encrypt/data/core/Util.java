package com.wangyi.component.encrypt.data.core;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.wangyi.component.encrypt.data.annotation.EncryptField;
import com.wangyi.component.encrypt.data.handler.EncryptBody;
import com.wangyi.component.encrypt.data.handler.EncryptHandler;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class Util {

    // 缓存: <EntityClass, Set<EncryptField>>
    private static final Map<Class<?>, Set<Field>> ENCRYPT_FIELD_CACHE = new ConcurrentHashMap<>();

    // 缓存: <queryStatementId, Map<paramName, EncryptField>>
    private static final Map<String, Map<String, EncryptField>> ENCRYPT_PARAM_CACHE = new ConcurrentHashMap<>();

    // 缓存：<queryStatementId, Pair<MapperClass, EntityClass>>
    private static final Map<String, Pair<Class, Class>> MAPPED_CLASS_CACHE = new ConcurrentHashMap<>();

    private static final List<String> suffixList = new ArrayList<>(2);

    static {
        suffixList.add("_mpCount");
        suffixList.add("_COUNT");
    }

    public static boolean encryptionRequired(Object parameter, SqlCommandType sqlCommandType) {
        return (sqlCommandType == SqlCommandType.INSERT
                || sqlCommandType == SqlCommandType.UPDATE
                || sqlCommandType == SqlCommandType.SELECT
                || sqlCommandType == SqlCommandType.DELETE)
                && decryptionRequired(parameter);
    }

    public static boolean decryptionRequired(Object parameter) {
        return !(parameter == null || parameter instanceof Double || parameter instanceof Integer
                || parameter instanceof Long || parameter instanceof Short || parameter instanceof Float
                || parameter instanceof Boolean || parameter instanceof Character
                || parameter instanceof Byte);
    }

    public static String getParamName(Parameter parameter) {
        Param paramAnnotation = parameter.getAnnotation(Param.class);
        return paramAnnotation != null ? paramAnnotation.value() : parameter.getName();
    }

    public static Parameter[] getParametersByMappedStatementId(String msId) throws ClassNotFoundException {
        String className = msId.substring(0, msId.lastIndexOf("."));
        String methodName = msId.substring(msId.lastIndexOf(".") + 1);
        Method method = findMethod(className, methodName);
        if (method == null) {
            return null;
        }
        return method.getParameters();
    }

    public static Method findMethod(String className, String methodName) throws ClassNotFoundException {
        String trueMethodName = suffixList.stream().filter(methodName::endsWith).findFirst()
                .map(suffix -> methodName.substring(0, methodName.length() - suffix.length()))
                .orElse(methodName);
        Method[] methods = Class.forName(className).getMethods();
        if (methods.length == 0) {
            return null;
        }
        return Arrays.stream(methods)
                .filter(method -> method.getName().equals(trueMethodName))
                .findFirst()
                .orElse(null);
    }

    public static String doFinal(EncryptHandler encryptor, Mode mode, EncryptBody encryptBody) {
        return Mode.DECRYPT.equals(mode) ? encryptor.decrypt(encryptBody) : encryptor.encrypt(encryptBody);
    }

    public static EncryptHandler getEncryptHandler(String encryptType) {
        Map<String, EncryptHandler> encryptHandlerMap = SpringUtil.getBeansOfType(EncryptHandler.class);
        Collection<EncryptHandler> encryptHandlerList = encryptHandlerMap.values();
        EncryptHandler encryptHandler = encryptHandlerList.stream()
                .filter(handler -> handler.support(encryptType))
                .findFirst().orElse(null);
        Assert.notNull(encryptHandler, "未找到 " + encryptType + " 加解密处理器");
        return encryptHandler;
    }

    /**
     * 获取类上的加密字段
     * @param parameterClass
     * @return
     */
    public static Set<Field> getEncryptField(Class<?> parameterClass) {
        return ENCRYPT_FIELD_CACHE.computeIfAbsent(parameterClass, aClass -> {
            Field[] declaredFields = ReflectUtil.getFields(aClass);
            if (declaredFields == null || declaredFields.length == 0) {
                return Collections.emptySet();
            }
            Set<Field> fieldSet = Arrays.stream(declaredFields)
                    .filter(field -> field.isAnnotationPresent(EncryptField.class))
                    .collect(Collectors.toSet());
            if (fieldSet.isEmpty()) {
                return Collections.emptySet();
            }
            for (Field field : fieldSet) {
                field.setAccessible(true);
            }
            return fieldSet;
        });
    }

    /**
     * mabatis-plus 执行 xxService.lambdaQuery() 获取实体上的加密字段
     * @param mappedStatement
     * @return
     */
    @SneakyThrows
    public static Set<Field> getEncryptField(MappedStatement mappedStatement) {
        String msId = mappedStatement.getId();
        Pair<Class, Class> pair = MAPPED_CLASS_CACHE.get(msId);
        if (pair == null) {
            String classPath = msId.substring(0, msId.lastIndexOf("."));
            Class<?> mapperClass = Class.forName(classPath);
            Type typeArgument = TypeUtil.getTypeArgument(mapperClass, 0);
            Class<?> entityClass = (Class) typeArgument;
            pair = Pair.of(mapperClass, entityClass);
            MAPPED_CLASS_CACHE.put(msId, pair);
        }
        return getEncryptField(pair.getValue());
    }

    /**
     * 获取 mybatis 查询参数上的加密字段
     * @param mappedStatement
     * @return
     */
    public static Map<String, EncryptField> gentEncryptField(MappedStatement mappedStatement) {
        String msId = mappedStatement.getId();
        Map<String, EncryptField> paramEncryptField = ENCRYPT_PARAM_CACHE.get(msId);
        if (null == paramEncryptField) {
            paramEncryptField = new HashMap<>();
            try {
                // 注解解析
                Parameter[] parameters = Util.getParametersByMappedStatementId(msId);
                if (parameters == null || parameters.length == 0) {
                    return Collections.emptyMap();
                }
                for (int paramIndex = 0; paramIndex < parameters.length; paramIndex++) {
                    Parameter parameter = parameters[paramIndex];
                    String paramName = Util.getParamName(parameter);
                    EncryptField encryptedField = parameter.getAnnotation(EncryptField.class);
                    if (encryptedField != null) {
                        paramEncryptField.put(paramName, encryptedField);
                        if (parameter.getType().equals(String.class)) {
                            String paramIndexKey = "param" + (paramIndex + 1);
                            paramEncryptField.put(paramIndexKey, encryptedField);
                        }
                    }
                }
            } catch (Exception e) {
                throw new MybatisCryptoException(e);
            }
        }
        return paramEncryptField;
    }

    @Accessors(chain = true)
    @Data
    public static class ColumMapping {
        private String aliasName;
        private String val;
    }

    /**
     * 根据Mybatis-plus的whereSql获得一个参数别名与字段名的映射map
     *
     * @return 别名, 字段名
     */
    public static Map<String, ColumMapping> parseParamAliasNameMap(AbstractWrapper ew, Map<String, Object> paramNameValuePairs) {
        Map<String, String> allParams = new HashMap<>();
        allParams.putAll(parseWhereSql(ew));
        allParams.putAll(parseSetSql(ew));

        Map<String, ColumMapping> columMap = new HashMap<>();
        for (Map.Entry<String, Object> item : paramNameValuePairs.entrySet()) {
            //参数别名
            String aliasName = item.getKey();
            //参数值
            String val = item.getValue() != null ? item.getValue().toString() : null;
            //字段名
            String columnName = allParams.get(aliasName);
            columMap.put(columnName, new ColumMapping().setAliasName(aliasName).setVal(val));
        }
        return columMap;
    }

    @SneakyThrows
    private static Map<String, String> parseWhereSql(AbstractWrapper ew) {
        Map<String, String> res = new HashMap<>();
        String sql = ew.getSqlSegment();
        if (StrUtil.isBlank(sql)) {
            return res;
        }
        Expression expression = CCJSqlParserUtil.parseCondExpression(replaceSql(sql));
        expression.accept(new ExpressionVisitorAdapter() {
            @Override
            public void visit(AndExpression expr) {
                if (expr.getLeftExpression() instanceof AndExpression) {
                    expr.getLeftExpression().accept(this);
                } else if ((expr.getLeftExpression() instanceof EqualsTo)) {
                    Pair<String, String> pair = getColumnName(expr.getLeftExpression());
                    res.put(pair.getValue(), pair.getKey());
                }
                Pair<String, String> pair = getColumnName(expr.getRightExpression());
                res.put(pair.getValue(), pair.getKey());
            }

            @Override
            public void visit(EqualsTo expr) {
                Pair<String, String> pair = getColumnName(expr);
                res.put(pair.getValue(), pair.getKey());
            }
        });
        return res;
    }

    private static Map<String, String> parseSetSql(AbstractWrapper ew) {
        String setSql = ew.getSqlSet();
        if (StrUtil.isBlank(setSql)) {
            return new HashMap<>(0);
        }
        List<String> setList = StrUtil.split(replaceSql(setSql), ",");
        Map<String, String> map = new HashMap<>(setList.size());
        setList.forEach(str -> {
            String columnName = StrUtil.split(str, "=").get(0).trim();
            String aliasName = StrUtil.split(str, "=").get(1).trim();
            map.put(aliasName, columnName);
        });
        return map;
    }

    /**
     * 返回where表达式中，字段名与参数名的映射关系
     *
     * @param expr
     * @return <字段名,别名> ，参数别名特指：MPGENVAL1、MPGENVAL2 ...
     */
    private static Pair<String, String> getColumnName(Expression expr) {
        if (!(expr instanceof EqualsTo)) {
            return null;
        }
        EqualsTo equalsTo = (EqualsTo) expr;
        String columnName = ((Column) equalsTo.getLeftExpression()).getColumnName();

        String aliasName;
        if (equalsTo.getRightExpression() instanceof Column) {
            aliasName = ((Column) equalsTo.getRightExpression()).getColumnName();
        } else {
            aliasName = equalsTo.getRightExpression().toString();
        }
        return Pair.of(columnName, aliasName);
    }

    private static String replaceSql(String sql) {
        return sql.replaceAll("#", "")
                .replaceAll("\\$", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("\\{", "")
                .replaceAll("}", "")
                .replaceAll("ew.paramNameValuePairs.", "");
    }

}

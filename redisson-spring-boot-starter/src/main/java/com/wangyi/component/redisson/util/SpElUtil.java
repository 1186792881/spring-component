package com.wangyi.component.redisson.util;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class SpElUtil {

    public static final String SPEL_FLAG = "#";

    /**
     * Spel解析器
     */
    private static final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 方法参数名解析器
     */
    private static final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 获取方法的全限定名
     * @param method 方法
     * @return
     */
    public static String getMethodKey(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    /**
     * 解析SPEL表达式
     *
     * @param spel          SPEL表达式
     * @param method        方法
     * @param args          实参数组
     */
    public static String parseSpEl(Method method, String spel, Object[] args) {
        if (!StringUtils.hasText(spel) || !spel.contains(SPEL_FLAG)) {
            return spel;
        }
        // 参数名
        String[] argNames = nameDiscoverer.getParameterNames(method);
        // 解析后的 SPEL
        Expression expression = parser.parseExpression(spel);
        // spring 表达式上下文
        EvaluationContext context = new StandardEvaluationContext();
        // 给上下文赋值变量
        if (null != argNames) {
            for (int i = 0; i < argNames.length; i++) {
                context.setVariable(argNames[i], args[i]);
            }
        }
        Object value = expression.getValue(context);
        return null == value ? spel : value.toString();
    }

}

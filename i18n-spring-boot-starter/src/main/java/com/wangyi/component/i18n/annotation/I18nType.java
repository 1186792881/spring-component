package com.wangyi.component.i18n.annotation;

import com.wangyi.component.i18n.constant.I18nTypeEnum;
import com.wangyi.component.i18n.constant.LanguageEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface I18nType {

    /**
     * 类型枚举
     * @return
     */
    I18nTypeEnum value();

    /**
     * 默认语言
     * @return
     */
    LanguageEnum language() default LanguageEnum.CHINESE_CHINA;

    /**
     * 获取code方法名
     * @return
     */
    String codeMethod() default "getCode";

    /**
     * 获取msg方法名
     * @return
     */
    String msgMethod() default "getMsg";

}

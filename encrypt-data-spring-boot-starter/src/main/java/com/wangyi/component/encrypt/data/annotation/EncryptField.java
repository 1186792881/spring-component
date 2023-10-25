package com.wangyi.component.encrypt.data.annotation;

import com.wangyi.component.encrypt.data.enums.EncryptKeyType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加密字段注解
 * 可以使用在字段和参数上
 */
@Documented
@Inherited
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {

    /**
     * 加密方式
     * @return
     */
    String encryptType();

    /**
     * 加密密钥类型
     * 非对称加解密时, 需要指定是公钥加解密还是私钥加解密
     * 对称加解密时, 使用相同的密钥加解密, 就是该默认值
     * @return
     */
    EncryptKeyType encryptKeyType() default EncryptKeyType.SECRET_KEY;

}

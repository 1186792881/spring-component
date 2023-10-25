package com.wangyi.component.encrypt.api.annotation;

import com.wangyi.component.encrypt.api.enums.EncryptKeyType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EncryptResponseBody {

    String encryptType();

    EncryptKeyType encryptKeyType() default EncryptKeyType.SECRET_KEY;
}

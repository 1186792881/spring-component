package com.wangyi.component.i18n.util;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.wangyi.component.base.exception.BizAssert;
import com.wangyi.component.i18n.annotation.I18nType;
import com.wangyi.component.i18n.config.properties.I18nProperties;
import com.wangyi.component.i18n.source.entity.I18n;
import lombok.SneakyThrows;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ScanUtil {

    /**
     * 扫描枚举和错误码
     *
     * @param i18nProperties
     * @return
     */
    @SneakyThrows
    public static List<I18n> scanI18nEnum(I18nProperties i18nProperties) {

        if (!(i18nProperties.isEnableScan() && StrUtil.isNotBlank(i18nProperties.getScanPackage()))) {
            return Collections.emptyList();
        }

        List<I18n> i18nList = new ArrayList<>();
        Set<Class<?>> set = ClassUtil.scanPackageByAnnotation(i18nProperties.getScanPackage(), I18nType.class);
        for (Class<?> clazz : set) {
            I18nType i18nType = AnnotationUtils.findAnnotation(clazz, I18nType.class);
            if (null != i18nType && ClassUtil.isEnum(clazz)) {
                Method codeMethod = ClassUtil.getDeclaredMethod(clazz, i18nType.codeMethod());
                Method msgMethod = ClassUtil.getDeclaredMethod(clazz, i18nType.msgMethod());
                if (codeMethod == null || msgMethod == null) {
                    continue;
                }

                String type = i18nType.value().getValue();
                String language = i18nType.language().getValue();

                // 得到 enum 的所有实例：一个数组，该数组包含组成此 Class 对象表示的枚举类的值
                Object[] objs = clazz.getEnumConstants();
                for (Object obj : objs) {
                    String code = (String) codeMethod.invoke(obj);
                    String msg = (String) msgMethod.invoke(obj);

                    // 判断 type, languag, code 是否已存在
                    long count = i18nList.stream().filter(i18n -> i18n.getType().equals(type) && i18n.getLanguage().equals(language) && i18n.getCode().equals(code)).count();
                    BizAssert.isTrue(count > 0,
                            "i18n.init.error",
                            "国际化编码重复 type:{}, language:{}, code:{}, msg:{}",
                            i18nType.value().getValue(), i18nType.language().getValue(), code, msg);

                    I18n i18n = new I18n();
                    i18n.setType(type);
                    i18n.setLanguage(language);
                    i18n.setCode(code);
                    i18n.setValue(msg);
                    i18nList.add(i18n);
                }
            }
        }

        return i18nList;
    }

}

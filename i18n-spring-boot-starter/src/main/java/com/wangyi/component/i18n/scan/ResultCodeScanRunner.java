package com.wangyi.component.i18n.scan;

import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.wangyi.component.base.exception.BizAssert;
import com.wangyi.component.base.exception.ResultCode;
import com.wangyi.component.i18n.config.I18nProperties;
import com.wangyi.component.i18n.constant.I18nTypeEnum;
import com.wangyi.component.i18n.core.I18nMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class ResultCodeScanRunner implements CommandLineRunner {

    private final I18nProperties i18nProperties;
    private final I18nMessageSource i18nMessageSource;

    @Override
    public void run(String... args) throws Exception {
        Map<String, String> codeMsgMap = scanResultCode();
        i18nMessageSource.initMessage(I18nTypeEnum.RESULT_CODE.getValue(), codeMsgMap, i18nProperties.getScanLanguage());
        log.info("初始化错误码 {} 条", codeMsgMap.size());
    }

    @SneakyThrows
    private Map<String, String> scanResultCode() {
        Map<String, String> codeMsgMap = new HashMap<>();

        if (!i18nProperties.isEnableScan() || StrUtil.isBlank(i18nProperties.getScanPackage())) {
            return codeMsgMap;
        }

        Set<Class<?>> set = ClassUtil.scanPackageBySuper(i18nProperties.getScanPackage(), ResultCode.class);
        for (Class<?> clazz : set) {
            if (ClassUtil.isEnum(clazz)) {
                Method codeMethod = ClassUtil.getDeclaredMethod(clazz, LambdaUtil.getMethodName(ResultCode::getCode));
                Method msgMethod = ClassUtil.getDeclaredMethod(clazz, LambdaUtil.getMethodName(ResultCode::getMsg));
                if (codeMethod == null || msgMethod == null) {
                    continue;
                }

                // 得到 enum 的所有实例：一个数组，该数组包含组成此 Class 对象表示的枚举类的值
                Object[] objs = clazz.getEnumConstants();
                for (Object obj : objs) {
                    String code = (String) codeMethod.invoke(obj);
                    String msg = (String) msgMethod.invoke(obj);
                    BizAssert.isTrue(codeMsgMap.containsKey(code), "i18n.scan.error", "错误码重复 code:{}, msg:{}", code, msg);
                    codeMsgMap.put(code, msg);
                }
            }
        }

        return codeMsgMap;
    }

}

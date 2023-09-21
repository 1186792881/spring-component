package com.wangyi.component.i18n.config;

import com.wangyi.component.i18n.constant.LanguageEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("i18n")
@Component
@Data
public class I18nProperties {
    private boolean enabled = true;
    private boolean enableScan = false;
    private String scanPackage;
    private String scanLanguage = LanguageEnum.CHINESE_CHINA.getValue();
}

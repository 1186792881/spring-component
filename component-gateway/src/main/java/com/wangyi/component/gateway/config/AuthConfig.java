package com.wangyi.component.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties("gateway.auth")
@RefreshScope
@Component
public class AuthConfig {

    /**
     * 是否启用权限校验, 默认 false 不校验
     */
    private boolean enable = false;

    /**
     * 忽略校验的路径
     */
    private List<String> ignorePath = new ArrayList<>();

}

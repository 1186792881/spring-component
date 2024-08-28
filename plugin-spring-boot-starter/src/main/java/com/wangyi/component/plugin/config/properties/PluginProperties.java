package com.wangyi.component.plugin.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("plugin")
@Component
@Data
public class PluginProperties {

}

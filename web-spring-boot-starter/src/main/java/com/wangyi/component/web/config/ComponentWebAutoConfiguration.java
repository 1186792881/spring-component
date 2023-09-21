package com.wangyi.component.web.config;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("com.wangyi.component.web")
@ServletComponentScan("com.wangyi.component.web")
@EnableSpringUtil
@Configuration
public class ComponentWebAutoConfiguration {

}

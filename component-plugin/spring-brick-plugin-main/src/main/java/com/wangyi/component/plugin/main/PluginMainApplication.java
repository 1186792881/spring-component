package com.wangyi.component.plugin.main;

import com.gitee.starblues.loader.launcher.SpringBootstrap;
import com.gitee.starblues.loader.launcher.SpringMainBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PluginMainApplication implements SpringBootstrap {

    public static void main(String[] args) {
        // 该处使用 SpringMainBootstrap 引导启动
        SpringMainBootstrap.launch(PluginMainApplication.class, args);
    }

    @Override
    public void run(String[] args) throws Exception {
        // 在该实现方法中, 和 SpringBoot 使用方式一致
        SpringApplication.run(PluginMainApplication.class, args);
    }

}

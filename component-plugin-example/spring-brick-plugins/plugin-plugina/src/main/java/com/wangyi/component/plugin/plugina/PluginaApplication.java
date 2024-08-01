package com.wangyi.component.plugin.plugina;

import com.gitee.starblues.bootstrap.SpringPluginBootstrap;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PluginaApplication extends SpringPluginBootstrap {

    public static void main(String[] args) {
        new PluginaApplication().run(args);
    }

}

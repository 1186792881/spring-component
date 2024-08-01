package com.wangyi.component.plugin.plugina.controller;

import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.wangyi.component.plugin.main.service.MainService;
import com.wangyi.component.plugin.plugina.service.ExampleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * com.gitee.starblues.bootstrap.processor.web.PluginControllerProcessor  180 行
 * 不加 RequestMapping 注解会导致加载不到这个 Controller
 */
@RestController
@RequestMapping
public class ExampleController {

    @Resource
    private ExampleService exampleService;

    @Resource
    @AutowiredType(AutowiredType.Type.MAIN)
    private MainService mainService;

    @GetMapping("/main/hello")
    public String mainHello() {
        return mainService.hello();
    }

    @GetMapping("/plugin/hello")
    public String exampleHello() {
        return exampleService.hello();
    }

}
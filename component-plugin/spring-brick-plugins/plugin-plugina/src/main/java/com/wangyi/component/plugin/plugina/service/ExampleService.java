package com.wangyi.component.plugin.plugina.service;

import com.wangyi.component.plugin.main.plugin.HelloInterface;
import org.springframework.stereotype.Service;

@Service
public class ExampleService implements HelloInterface {

    @Override
    public String hello() {
        return "plugina hello";
    }
}

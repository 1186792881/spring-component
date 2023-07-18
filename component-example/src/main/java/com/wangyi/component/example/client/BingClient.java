package com.wangyi.component.example.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Var;
import org.springframework.stereotype.Component;

@Component
@BaseRequest(baseURL = "https://cn.bing.com")
public interface BingClient {

    @Get("/search?q=${q}")
    String search(@Var("q") String q);
}

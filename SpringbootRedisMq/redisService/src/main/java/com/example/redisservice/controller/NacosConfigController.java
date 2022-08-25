package com.example.redisservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NacosConfigController {
    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @GetMapping("/getConfig")
    public String getConfig() {
        return applicationContext.getEnvironment().getProperty("nacos-server-addr");
    }
}

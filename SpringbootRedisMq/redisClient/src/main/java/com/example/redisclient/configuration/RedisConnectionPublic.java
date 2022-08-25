package com.example.redisclient.configuration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
public class RedisConnectionPublic {
    @Resource
    private Environment environment;
    public RedisCommands<String, String> RedisConnectionPublicClass() {
        String host = this.environment.getProperty("spring.redis.host");
        String port = this.environment.getProperty("spring.redis.port");
        StatefulRedisConnection<String, String> connect =
                RedisClient.create("redis://"+host+":"+port+"").connect();
        RedisCommands<String, String> commands = connect.sync();
        //System.out.println("commands:"+commands);
        return commands;
    }
}

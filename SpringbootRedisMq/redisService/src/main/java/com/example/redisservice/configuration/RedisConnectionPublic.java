package com.example.redisservice.configuration;

import com.example.redisservice.entity.RedisDatabaseObject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RedisConnectionPublic {
    @Autowired
    private Environment environment;
    public RedisCommands<String, String> RedisConnectionPublicClass() {
        String host = environment.getProperty("spring.redis.host");
        String port = environment.getProperty("spring.redis.port");
        StatefulRedisConnection<String, String> connect =
                RedisClient.create("redis://"+host+":"+port+"").connect();
        RedisCommands<String, String> commands = connect.sync();

        return commands;

    }
}

package com.example.redisservice;

import com.example.redisservice.controller.RedisServiceContorller;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class RedisServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(RedisServiceApplication.class, args);
    }

}

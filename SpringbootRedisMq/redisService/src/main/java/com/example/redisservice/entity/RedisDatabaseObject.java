package com.example.redisservice.entity;

import lombok.Data;
import org.springframework.core.env.Environment;

@Data
public class RedisDatabaseObject {
    private String host;
    private String port;
    private String password;
    private String database;

}

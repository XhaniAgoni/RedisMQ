package com.example.redisservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;

@SpringBootTest
class RedisServiceApplicationTests {

    @Test
    void contextLoads() throws IOException {
        String cmd = ("cmd.exe /c start C:\\Users\\13053\\Desktop\\资料\\redis\\redis-cli.exe xreadgroup group2 consumer1 1 queuetest");
        Process exec = Runtime.getRuntime().exec(cmd);
        InputStream inputStream = exec.getInputStream();
        System.out.println("exec"+exec);
    }

}

package com.example.redisclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
public class RedisClientApplication {

    public static void main(String[] args) {
//        StatefulRedisConnection<String, String> connect =
//                RedisClient.create("redis://localhost:6379").connect();
//        RedisCommands<String, String> commands = connect.sync();
//        //创建分组
////        commands.xgroupCreate(XReadArgs.StreamOffset.from("queuetest", "0"), "group3");
//        //按组读取消息
//        List<StreamMessage<String, String>> xreadgroup =
//                commands.xreadgroup(Consumer.from("group2", "consumer2"),
//                XReadArgs.StreamOffset.lastConsumed("queuetest"));
//        System.out.println("收到的订阅消息："+xreadgroup);
//        //String id = xreadgroup.get(0).getId();
//        //System.out.println(id);
//        //commands.xack("queuetest", "group3", "0-0");
//        //System.out.println("消息已经消费"+commands.xack("queuetest", "group3", id));
//        //System.out.println("收到的订阅消息id:"+xreadgroup.get(0).getId());
//        if (xreadgroup.size() > 0) {
//            for (StreamMessage<String, String> streamMessage : xreadgroup) {
//                System.out.println("收到的订阅消息id："+streamMessage.getId());
//                //删除消息
//                //commands.xdel("queuetest", streamMessage.getId());
//                //System.out.println("redis删除了消息id："+streamMessage.getId());
//            }
//        }
//        //commands.xdel("queuetest", xreadgroup.get(0).getId());
//        //commands.xdel("queuetest", "0-0");
//
//
        SpringApplication.run(RedisClientApplication.class, args);
    }

}

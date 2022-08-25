package com.example.redisservice.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.redisservice.configuration.RedisConnectionPublic;
import com.example.redisservice.entity.FirstRedisKV;
import com.example.redisservice.entity.Result;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Api(tags = "redisService")
@RestController
public class RedisServiceContorller {
    @Autowired
    private RedisConnectionPublic commands;

    @SentinelResource(value = "xgroupWrite")
    @ApiOperation(value = "写入数据单key-value，必传参数：name（不建议以sys开头），key，value",
            notes = "写入数据单key-value，必传参数：name（不建议以sys开头），key，value",
            response = Result.class)
    @PostMapping("/redis/xgroupWrite")
    //单组数据写入redis
    public Result redisService(@RequestBody FirstRedisKV map) {
        //实体类写入redis
        String next = map.getKey();
        String next1 = map.getValue();
        String next2 = map.getName();
        if (next2 == null || next2.equals("") || next2.equals("string")) {
            return Result.refererror("队列名称不能为空或'string'");
        }

        //非实体类写入redis
        //String next = map.get("next").toString();
        //String next1 = map.get("next1").toString();
        //String next2 = map.get("next2").toString();

//        Scanner scanner = new Scanner(System.in);
//        System.out.println("请输入valueKey：");
//        String next = scanner.next();
//        System.out.println("请输入valueValue：");
//        String next1 = scanner.next();
//        System.out.println("请输入key：");
//        String next2 = scanner.next();

        Map<String, String> map1 = Collections.singletonMap(next,next1);//"name","test"
        String xadd = commands.RedisConnectionPublicClass().xadd(next2, map1);//"queuetest"
        //暂不做失败处理
        return Result.ok(
                "传入队列key："+next+" ,redisid："+xadd+"，传入参数："+map1+"，传出参数data",map1);
    }
    //多组数据写入redis
    @SentinelResource(value = "xgroupWrites")
    @ApiOperation(value = "写入数据多key-value，必传参数：name（不建议以sys开头），key1-value1，key2-value2... '示例{“queuetest”，“name”，“zhangsan”，“age”，“18”} '",
            notes = "写入数据多key-value，，必传参数：name（不建议以sys开头），key1-value1，key2-value2... '示例{“queuetest”，“name”，“zhangsan”，“age”，“18”}'",
            response = Result.class)
    @PostMapping("/redis/xgroupWrites")
    public Result redisService1(@RequestBody List<String> map) {
        //非实体类写入redis
        List<String> strings = new ArrayList<>(map);
        //创建map
        Map<String, String> stringStringHashMap = new HashMap<>();
        String name = strings.get(0);
        //截取除第一个参数以外的其他参数
        strings.remove(0);
        //System.out.println(strings);
        //需要对截取后的string进行数值校验
        if (strings.size() % 2 != 0) {
            return Result.refererror(
                    "error:在数组中，除第一个参数外(redis队列)，参数个数不是偶数个，参数需要满足key-value的形式" +
                    "示例：[“queuetest[队列名]”,“name[第一个key]”,“test-01[第一个value]”,“name[第二个key]”,“test-01[第二个value]”...]");
        }
        for (int i=3;i<strings.size()+3;i++){
            if (i%2==1){
                //生成集合类型的map
                stringStringHashMap.put(strings.get(i-3), strings.get(i-2));
            }
        }
        //System.out.println(stringStringHashMap);

        String xadd = commands.RedisConnectionPublicClass()
                .xadd(map.get(0), stringStringHashMap);//"queuetest" + stringStringHashMap必须为集合类型
        return Result.ok(
                "传入队列key："+name+"redisid："+xadd+"，传入参数："+strings+"，传出参数data",stringStringHashMap);
    }

    @SentinelResource(value = "keys")
    @ApiOperation(value = "查询redis已存在的队列key,可传参数：name，name='*'查询所有队列key，默认支持模糊查询",
            notes = "查询redis已存在的队列key，可传参数：name，name='*'查询所有队列key，默认支持模糊查询",
            response = Result.class)
    @PostMapping("/redis/keys")
    public Result keys(@RequestBody FirstRedisKV map) {
        String name = map.getName();
        if (name ==null || name.equals("")  || name.equals("string")) {
            name = "";
        }

        String name1 =name+"*";
        List<String> keys = commands.RedisConnectionPublicClass().keys(name1);
        if (keys.size() == 0) {
            return Result.refererror("队列不存在");
        }else {
            return Result.ok("传入参数："+name+"，传出参数data",keys);
        }

    }


}

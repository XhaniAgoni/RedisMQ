package com.example.redisservice.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel("FirstRedisKV")
@Data
//1.0test 简单的一个redis的kv对象 用于测试 key value 对象的存储, name表示队列名称 key表示key value表示value
public class FirstRedisKV {
    @ApiModelProperty("key")
    private String key;
    @ApiModelProperty("value")
    private String value;
    @ApiModelProperty("队列名称")
    private String name;
}

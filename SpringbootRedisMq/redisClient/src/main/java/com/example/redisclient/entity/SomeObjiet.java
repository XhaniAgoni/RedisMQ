package com.example.redisclient.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel(value = "RedisDatabaseObject对象", description = "redis传入对象")
@Data
public class SomeObjiet {
    //redis队列名称
    @ApiModelProperty(value = "redis队列名称")
    private String name;

    //redis自增id 默认为 0 或 0-0
    @ApiModelProperty(value = "redis队列id 格式：0-0 格式：时间戳-redisid自增id 在xreadgroup中为'>'的大于号")
    private String offset;

    //redis分组名称
    @ApiModelProperty(value = "redis分组名称")
    private String groupname;

    //redis消费者名称
    @ApiModelProperty(value = "redis消费者名称")
    private String consumer;

    //redis消费者状态pending
    @ApiModelProperty(value = "redis消费者状态pending")
    private String pending;

    //redis消费者状态idle
    @ApiModelProperty(value = "redis消费者状态idle")
    private String idle;

    //行数 默认为1行
    @ApiModelProperty(value = "行数 默认为1行")
    private Long Count;

    //起始行数 默认为 - 号
    @ApiModelProperty(value = "起始行数 默认为 - 号")
    private String start;

    //结束行数 默认为 + 号
    @ApiModelProperty(value = "结束行数 默认为 + 号")
    private String end;

    //重连次数 默认为0
    @ApiModelProperty(value = "重连次数 默认为0")
    private Long retrycount;

    //是否近似修剪
    //@ApiModelProperty(value = "是否近似修剪 不传参数，参数取决于approxvalue")
    //private Boolean approx;

    //近似修剪值
    @ApiModelProperty(value = "近似修剪值 默认值为~")
    private String approxvalue;

}

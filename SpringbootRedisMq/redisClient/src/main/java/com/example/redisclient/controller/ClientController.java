package com.example.redisclient.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.redisclient.auth.GroupConsumerAuth;
import com.example.redisclient.entity.Result;
import com.example.redisclient.entity.SomeObjiet;
import io.lettuce.core.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.redisclient.configuration.RedisConnectionPublic;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "redisClient")
@RestController
public class ClientController {
    //获取redis连接 公共连接
    @Autowired
    private RedisConnectionPublic commands;
    @Autowired
    private GroupConsumerAuth groupConsumerAuth;


    //创建分组 要权限 ,必传参数：groupname，name
    @SentinelResource(value = "xgroupCreate")
    @ApiOperation(value = "创建分组，默认读取整个队列key，必传参数：groupname，name",
            notes = "创建分组，默认读取整个队列key，必传参数：groupname，name",
            response = Result.class)
    @PostMapping("/redis/xgroupCreate")
    public Result xgroupCreate(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String offset = map.getOffset();
        String groupname = map.getGroupname();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        //创建分组test底层结构
        //commands.xgroupCreate(XReadArgs.StreamOffset.from("queuetest", "0"), "group3");
        //需获取redis连接
        //从头读取,offset为0,暂不做不从头读取的操作
        //判断组是否存在
        if (groupConsumerAuth.checkGroup2(map)) {
            return Result.refererror("该分组已存在");
        }else {
        offset = "0";
        String s = commands.RedisConnectionPublicClass()
                .xgroupCreate(XReadArgs.StreamOffset.from(name, offset), groupname);
        return Result.ok("创建分组成功的key："+name,s);
        }
    }

    //删除分组 要权限
    @SentinelResource(value = "xgroupDel")
    @ApiOperation(value = "删除分组，删除分组中是否有消费者正在消费，如果有则不能删除，如果没有则删除，但可以将该组强制删除，消费者也不会保留，必传参数：groupname，name",
            notes = "删除分组，删除分组中是否有消费者正在消费，如果有则不能删除，如果没有则删除，但可以将该组强制删除，消费者也不会保留，必传参数：groupname，name",
            response = Result.class)
    @PostMapping("/redis/xgroupDel")
    public Result xgroupDel(@RequestBody SomeObjiet map,Boolean isDel) {
        String name = map.getName();
        String offset = map.getOffset();
        String groupname = map.getGroupname();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        if (isDel==null){
            isDel=false;
        }
        if (isDel==false){
            isDel = groupConsumerAuth.checkGroupDelete(map);
        }
        if (isDel==true){
            //需获取redis连接
            Boolean aBoolean = commands.RedisConnectionPublicClass()
                    .xgroupDestroy(name, groupname);
            if (aBoolean) {
                return Result.ok(
                        "success 删除" + name, groupname);
            } else {
                return Result.fail(
                        "fail 删除失败，可能原因是没有该群组");
            }
        }return Result.refererror("请传入正确的群组，或者传入的群组中存在消费者正在消费和消费的项，即pending不为0，请检查或执行 /xpending 命令查看消费者");
    }

    //查询分组信息 要权限，必传参数：name
    @SentinelResource(value = "xinfoGroups")
    @ApiOperation(value = "查询分组信息，必传参数：name",
            notes = "查询分组信息，必传参数：name",
            response = Result.class)
    @PostMapping("/redis/xinfoGroups")
    public Result xinfoGroups(@RequestBody SomeObjiet map){
        String name = map.getName();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        //查询分组信息
        //commands.xgroupInfo("queuetest");
        List<Object> list = commands.RedisConnectionPublicClass()
                .xinfoGroups(name);
        if (list.size()>0) {
            return Result.ok(
                    "查询分组信息"+name,list);
        } else {
            return Result.refererror(
                    "error 查询分组信息有误，可能原因是没有队列或群组",list);
        }
    }

    @SentinelResource(value = "xinfoConsumers")
    //查询组中消费者信息 要权限
    @ApiOperation(value = "查询组中消费者信息，必传参数：groupname，name",
            notes = "查询组中消费者信息，必传参数：groupname，name",
            response = Result.class)
    @PostMapping("/redis/xinfoConsumers")
    public Result xinfoConsumers(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String groupname = map.getGroupname();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        //查询组中消费者信息
        //commands.xgroupConsumers("queuetest");
        //查询队列key中是否有群组存在
        if (groupConsumerAuth.checkGroup2(map)){
            List<Object> list = commands.RedisConnectionPublicClass()
                    .xinfoConsumers(name, groupname);
            if (list.size()>0) {
            return Result.ok("返回key；"+name,list);
            } else {
                return Result.refererror(
                        "fail 查询组中消费者信息有误，可能原因是没有群组或群组的消费者",list);
            }
        }return Result.refererror("请传入正确的群组");
    }

    @SentinelResource(value = "xgroupSetid")
    //群组重新读取全部id，可以读取所有历史消息 仅group
    @ApiOperation(value = "重置整个队列，群组重新读取全部id，正在消费的消费者也会重新读取队列，可以读取所有历史消息 仅group，必传参数：groupname，name",
            notes = "重置整个队列，群组重新读取全部id，正在消费的消费者也会重新读取队列，可以读取所有历史消息 仅group，必传参数：groupname，name",
            response = Result.class)
    @PostMapping("/redis/xgroupSetid")
    public Result xgroupSetid(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String groupname = map.getGroupname();
        String offset = "0";
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        //commands.xgroupsetid(XReadArgs.StreamOffset.from("queuetest", "0"), "group1");
        String s = commands.RedisConnectionPublicClass()
                .xgroupSetid(XReadArgs.StreamOffset.from(name, offset), groupname);
        return Result.ok("返回的key："+name+"返回的群组："+groupname,s);
    }

//    //使用 xinfo 命令查询新创建的分组信息，可以看到分组名字，消费者数量，最后加入的消息的 ID 值
//    @PostMapping("/redis/xinfogroup")
//    public List<Object> xinfogroup(@RequestBody SomeObjiet map) {
//        String name = map.getName();
//        //commands.xinfogroup("queuetest");
//        List<Object> list = commands.RedisConnectionPublicClass()
//                .xinfoGroups(name);
//        return list;
//    }
//
//    //使用 xinfo 命令查看新加入的消费者的信息，可以看到消费者名字，处于 pending（待处理） 状态的消息数量
//    @PostMapping("/redis/xinfoconsumer")
//    public List<Object> xinfoconsumer(@RequestBody SomeObjiet map) {
//        String name = map.getName();
//        String groupname = map.getGroupname();
//        //commands.xinfoconsumer("queuetest");
//        List<Object> list =commands.RedisConnectionPublicClass()
//                .xinfoConsumers(name, groupname);
//        //list转换成json格式
//
//        return list;
//    }
//
    //使用 delconsumer 命令删除分组中的消费者，删除消费者不会将未消费的消息返回队列，必须进行xclaim将消息转移，或xtrim修剪消息，或xack确认消息，或xdel删除id，必传参数：groupname，name，consumer
    //不可复原谨慎使用 要权限
    @SentinelResource(value = "delconsumer")
    @ApiOperation(value = "使用 delconsumer 命令删除分组中的消费者，删除消费者不会将未消费的消息返回队列，必须进行xclaim将消息转移，或xtrim修剪消息，或xack确认消息，或xdel删除id，必传参数：groupname，name，consumer",
            notes = "使用 delconsumer 命令删除分组中的消费者，删除消费者不会将未消费的消息返回队列，必须进行xclaim将消息转移，或xtrim修剪消息，或xack确认消息，或xdel删除id，必传参数：groupname，name，consumer",
            response = Result.class)
    @PostMapping("/redis/delconsumer")
    public Result delconsumer(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String groupname = map.getGroupname();
        String consumer = map.getConsumer();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        //判断群组是否存在
        if(groupConsumerAuth.checkGroup2(map)){
            if (!groupConsumerAuth.checkConsumer(map)) {
                try {
                    //commands.delconsumer("queuetest");
                    Boolean aBoolean = commands.RedisConnectionPublicClass()
                            .xgroupDelconsumer(name, Consumer.from(groupname, consumer));
                    if (!aBoolean) {
                        return Result.ok(
                                "success:已删除该消费者" + name, consumer);
                    } else {
                        return Result.fail(
                                "fail:删除消费者失败，原因：组中不存在该消费者");
                    }
                } catch (Exception e) {
                    return Result.refererror(
                            "error:分组不存在或有误,key：" + name, groupname);
                }
            }else {
                return Result.refererror(
                        "reject:不建议删除该消费者，因为该消费者有存在正在消费的信息，删除该消费者不会将未消费完成的消息返回队列，" +
                                "必须进行xclaim将消息转移，或xtrim修剪正在消费消息，或xack确认正在消费消息，或xdel删除id，使得pending为0，才可以删除该消费者");
            }
        }return Result.refererror(
                "error:分组不存在或有误,key："+name,groupname);
    }

    //读取所有信息 最高
    @SentinelResource(value = "xinfoStream")
    @ApiOperation(value = "读取所有信息 最高，必传参数：name",
            notes = "读取所有信息 最高，必传参数：name",
            response = Result.class)
    @PostMapping("/redis/xinfoStream")
    public Result xinfoStream(@RequestBody SomeObjiet map) {
        String name = map.getName();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        //commands.xinfoStream("queuetest");
        List<Object> list = commands.RedisConnectionPublicClass()
                .xinfoStream(name);
        return Result.ok("返回所有消息"+name,list);
    }

    //xpending 命令可以查看对应分组中未确认的消息的数量和其所对应的消费者的名字还有起始和终止 ID
    @SentinelResource(value = "xpending")
    @ApiOperation(value = "xpending 命令可以查看对应分组中未确认的消息的数量和其所对应的消费者的名字还有起始和终止 ID，必传参数：groupname，name",
            notes = "xpending 命令可以查看对应分组中未确认的消息的数量和其所对应的消费者的名字还有起始和终止 ID，必传参数：groupname，name",
            response = Result.class)
    @PostMapping("/redis/xpending")
    public Result xpending(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String groupname = map.getGroupname();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        if (groupConsumerAuth.checkGroup2(map)){
        //commands.xpending("queuetest", "group1", "consumer1", "0", "10");
        List<Object> list = commands.RedisConnectionPublicClass()
                .xpending(name,groupname);
            return Result.ok("返回所有消息的key："+name,list);
        }
        return Result.refererror("分组不存在或有误,key："+name,groupname);
    }

    //使用 xpending 命令可以查看处于未确认状态的消息的具体信息
    @SentinelResource(value = "xpendinginfo")
    @ApiOperation(value = "使用 xpendinginfo 命令可以查看处于未确认状态的消息的具体信息，必传参数：groupname，name，consumer，start默认为'-'，end=默认为'+'，count",
            notes = "使用 xpendinginfo 命令可以查看处于未确认状态的消息的具体信息，必传参数：groupname，name，consumer，start默认为'-'，end=默认为'+'，count",
            response = Result.class)
    @PostMapping("/redis/xpendinginfo")
    public Result xpendinginfo(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String groupname = map.getGroupname();
        String consumer = map.getConsumer();
        String start = map.getStart();
        String end = map.getEnd();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        if (start== null&& start.equals("string")){
            start = "-";
        }
        if (end == null && end.equals("string")){
            end = "+";
        }
        Range<String> startAndEnd = Range.create(start, end);
        Long count =  map.getCount();
        List<Object> list = commands.RedisConnectionPublicClass()
                .xpending(name,Consumer.from(groupname,consumer),startAndEnd, Limit.from(count));
        return Result.ok(
                "success:查询成功",list);
    }

    //使用 readgroup 命令读取最新的消息
    @SentinelResource(value = "readgroup")
    @ApiOperation(value = "使用 xreadgroup 命令读取最新的消息，必传参数：groupname，name，consumer，选传参数，默认为：offset='>'，count=1",
            notes = "使用 readgroup 命令读取最新的消息 offset需要为'>'才可以按队列顺序获取，若offset为0，则会读取该消费者所读取的消息，根据count显示数量" +
                    "默认情况下，不传count都为1,若count不为1，且只有第一次可以读取count不为1的数量，第二次读取count始终为1，必传参数：groupname，name，consumer，offset默认为 '>'可选值0，count默认为 1",
            response = Result.class)
    @PostMapping("/redis/xreadgroup")
    public Result xreadgroup(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String groupname = map.getGroupname();
        String consumer = map.getConsumer();
        String offset = map.getOffset();
        Long count = map.getCount();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        if (offset == null || offset.equals("string")){
            offset = ">";
        }
        if (count == null || count==0){
            count = 1L;
        }
        //XReadArgs count1 = new XReadArgs().count(count);--方法改变,失效
        //验证群是否存在，若不存在则创建群
        boolean b1 = groupConsumerAuth.checkGroup1(map);
            //验证消费者是否存在，若不存在则创建消费者
            boolean b = groupConsumerAuth.checkGroup(map);
            if (b1&&b) {
                //验证消费者pending状态是否为0，不为0则将offset设置为'0'，否则设置为'>'
                boolean b2 = groupConsumerAuth.checkConsumer(map);
                if (b2){
                    offset = "0";
                    count = 1L;
                }else {
                    offset = ">";
                    if (count == 0){
                        count = 1L;
                    }
                }
                //commands.readgroup("queuetest", "group1", "consumer1", "10");
                List<StreamMessage<String, String>> list = commands.RedisConnectionPublicClass()
                        .xreadgroup(Consumer.from(groupname, consumer),
                                XReadArgs.Builder.count(count),
                                XReadArgs.StreamOffset.from(name, offset)
                        );
                if (list.size() == 0) {
                    return Result.fail(
                    "传入的参数有误，请先执行/xinfoStream查看全部信息,\t" +
                            "或/xinfoGroup查看群属,/xinfoConsumer查看消费者,\t" +
                            "也可以执行/xpending和/xpendinginfo查看未读取的详细信息,\t" +
                            "之后在执行/xreadgroup查看消息\t"
                    );
                } else {
                    return Result.ok("已成功消费"+name+"群组"+groupname+"消费者"+consumer+"消费数量"+count,list);
                }
            } else {
                return Result.fail(
                        "传入的参数有误，请先执行 /xinfoStream 查看全部信息,\t" +
                        "或 /xinfoGroup 查看群属, /xinfoConsumer 查看消费者,\t" +
                        "也可以执行 /xpending 和 /xpendinginfo 查看未读取的详细信息,\t" +
                        "之后在执行 /xreadgroup 查看消息\t"
                );
            }

    }

    //返回消息，从消息队列中移除消息，仅关于group，消息被消费后将不再出现在group中，但是仍然可以被其他group的消费者消费。
    @SentinelResource(value = "xack")
    @ApiOperation(value = "返回消息，从消息队列中移除消息，仅关于group，消息被消费后将不再出现在group中，但是仍然可以被其他group的消费者消费，必传参数：groupname，name，offset='具体的id'",
            notes = "返回消息，从消息队列中移除消息，仅关于group，消息被消费后将不再出现在group中，但是仍然可以被其他group的消费者消费，必传参数：groupname，name，offset='具体的id'",
            response = Result.class)
    @PostMapping("/redis/xack")
    public Result xack(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String groupname = map.getGroupname();
        String id = map.getOffset();
        //commands.xack("queuetest", "group1",, "?-?");
        Long xack = commands.RedisConnectionPublicClass()
                .xack(name, groupname, id);
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        if (xack==0){
            return Result.fail(
                    "fail：该消息已经被确认或消息有误,请检查消息ID是否正确,\t" +
                    "或执行/xinfoStream查看全部信息,\t" +
                    "/xinfoGroup查看群属,/xinfoConsumer查看消费者,\t" +
                    "也可以执行/xpending和/xpendinginfo查看未读取的详细信息,\t" +
                    "之后在执行/xreadgroup查看消息,\t" +
                    "最后执行/xack查看确认的消息"
            );
        }else {
            return Result.ok("success：已确认该消息",id);
        }
    }

    //删除id，当节点所有id都删除后，该节点将被删除
    @SentinelResource(value = "xdel")
    @ApiOperation(value = "删除id，当节点所有id都删除后，该节点将被删除，必传参数：name，offset='具体的id'，isTrue默认为false，如果为null也为false", notes = "删除id，当节点所有id都删除后，该节点将被删除，必传参数：name，offset='具体的id'，isTrue默认为false，如果为null也为false",response = Result.class)
    @PostMapping("/redis/xdel")
    public Result xdel(@RequestBody SomeObjiet map,Boolean isTrue) {
        String name = map.getName();
        String id = map.getOffset();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        if (isTrue==null){
            isTrue=false;
        }
        if (!isTrue){
        isTrue=groupConsumerAuth.checkDelete(map);
        }
        //commands.xdel("queuetest", "?-?");
        if (isTrue){
            Long xdel = commands.RedisConnectionPublicClass()
                    .xdel(name, id);
            if (xdel==0){
                return Result.fail(
                        "fail：该消息已经被确认或消息有误,请检查消息ID是否正确,\t" +
                        "或执行 /xinfoStream 查看全部信息,\t" +
                        " /xinfoGroup 查看群属, /xinfoConsumer 查看消费者,\t" +
                        "也可以执行 /xpending 和 /xpendinginfo 查看未读取的详细信息,\t" +
                        "之后在执行 /xreadgroup 查看消息,\t" +
                        "最后执行/xack 查看确认的消息"
                        );

            }else {
                return  Result.ok("已成功删除该消息"+name,id);
//                        "success：已删除该消息"+id;
            }
        }return Result.refererror(
                "error：请检查参数是否正确，可能原因为该id消费者有消费者在消费，请先执行 /xack 确认的消息，或选择强制删除 isTrue=true");
    }

    //删除key 当key被删除后，内存将释放，并且不会再被使用,如果key不存在，则不会发生任何事情，谨慎使用
    @SentinelResource(value = "delkey")
    @ApiOperation(value = "删除key 当key被删除后，内存将释放，并且不会再被使用，如果key不存在，则不会发生任何事情，谨慎使用，必传参数：name", notes = "删除key 当key被删除后，内存将释放，并且不会再被使用，如果key不存在，则不会发生任何事情，谨慎使用，必传参数：name", response = Result.class)
    @PostMapping("/redis/delkey")
    public Result delkey(@RequestBody SomeObjiet map) {
        String name = map.getName();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        //commands.del("queuetest");
        Long delkey = commands.RedisConnectionPublicClass()
                .del(name);
        if (delkey==0){
            return Result.fail("无法删除该key，可能原因为该key不存在");
//                    "fail：该消息已经被确认或消息有误,请检查key和消息ID是否正确,\t" +
//                    "或执行/xinfoStream查看全部信息,\t" +
//                    "/xinfoGroup查看群属,/xinfoConsumer查看消费者,\t" +
//                    "也可以执行/xpending和/xpendinginfo查看未读取的详细信息,\t" +
//                    "之后在执行/xreadgroup查看消息,\t" +
//                    "最后执行/xack查看确认的消息";

        }else {
            return Result.ok("已删除"+name,delkey);
        }

    }

    //返回 key 中消息的数量，如果 key 不存在，则会返回 0。即使 key 中消息的数量为 0，key 也不会被自动删除，因为可能还存在和 key 关联的消费者组
    @SentinelResource(value = "xlen")
    @ApiOperation(value = "返回 key 中消息的数量，如果 key 不存在，则会返回 0。即使 key 中消息的数量为 0，key 也不会被自动删除，因为可能还存在和 key 关联的消费者组，必传参数：name", notes = "返回 key 中消息的数量，如果 key 不存在，则会返回 0。即使 key 中消息的数量为 0，key 也不会被自动删除，因为可能还存在和 key 关联的消费者组，必传参数：name",response = Result.class)
    @PostMapping("/redis/xlen")
    public Result xlen(@RequestBody SomeObjiet map) {
        String name = map.getName();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        //commands.xlen("queuetest");
        Long xlen = commands.RedisConnectionPublicClass()
                .xlen(name);
        if (xlen==0){
            return Result.fail("队列key"+name+"没有消息");
        }else {
            return Result.ok("已返回key:"+name+"的消息数量",xlen);
//                    "success：已返回key:"+name+"的消息数量:"+xlen;
        }

    }

    //该命令返回与给定的 ID 范围相匹配的消息。ID 的范围由 start 和 end 参数来指定
    @SentinelResource(value = "xrange")
    @ApiOperation(value = "该命令返回与给定的 ID 范围相匹配的消息。ID 的范围由 start 和 end 参数来指定，必传参数：name，start=起始的id默认为最小 '-'，end=结尾的id默认为最大 '+'，选传参数：count，不传默认显示全部",
            notes = "该命令返回与给定的 ID 范围相匹配的消息。ID 的范围由 start 和 end 参数来指定，必传参数：name，start=起始的id默认为最小 '-'，end=结尾的id默认为最大 '+'，选传参数：count，不传默认显示全部",
            response = Result.class)
    @PostMapping("/redis/xrange")
    public Result xrange(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String start = map.getStart();
        String end = map.getEnd();
        if (!groupConsumerAuth.checkQueue(map)){
            return Result.refererror("队列key:"+name+"不存在",null);
        }
        if(start==null || start.equals("string")){
            start="-";
        }
        if (end==null || end.equals("string")){
            end="+";
        }
        Long count = map.getCount();
        Range<String> stringRange = Range.create(start, end);
        //commands.xrange("queuetest",0,10);
        if (count!=null && count>0){
        List<StreamMessage<String, String>> xrange = commands.RedisConnectionPublicClass()
                .xrange(name, stringRange, Limit.from(count));
            if (xrange.size()==0){
                return Result.fail(
                        "fail：队列为空,请检查key和消息ID是否正确,\t" +
                        "或执行 /xinfoStream 查看全部信息,\t" +
                        " /xinfoGroup 查看群属, /xinfoConsumer 查看消费者,\t" +
                        "也可以执行 /xpending 和 /xpendinginfo 查看未读取的详细信息,\t" +
                        "之后在执行 /xreadgroup 查看消息,\t" +
                        "最后执行 /xack 查看确认的消息"
                );
            }else {
                return Result.ok("已返回key:"+name+"的消息数量:"+xrange.size(),xrange);
//                        "success：已返回key:"+name+"的消息数量:"+xrange;
            }
        }else {
            List<StreamMessage<String, String>> xrange = commands.RedisConnectionPublicClass()
                    .xrange(name, stringRange);
            if (xrange.size()==0){
                return Result.fail(
                        "fail：队列为空,请检查key和消息ID是否正确,\t" +
                        "或执行 /xinfoStream 查看全部信息,\t" +
                        " /xinfoGroup 查看群属, /xinfoConsumer 查看消费者,\t" +
                        "也可以执行 /xpending 和 /xpendinginfo 查看未读取的详细信息,\t" +
                        "之后在执行 /xreadgroup 查看消息,\t" +
                        "最后执行 /xack 查看确认的消息"
                );
            }else {
                return Result.ok("已返回key:"+name+"的消息数量:"+xrange.size(),xrange);
//                        "success：已返回key:"+name+"的消息数量:"+xrange;
            }
        }
    }

    //xrevrange 命令和 xrange 命令语法完全相同，只有一点不同，xrevrange 是反向遍历的，不再赘述。
    @SentinelResource(value = "xrevrange")
    @ApiOperation(value = "xrevrange 命令和 xrange 命令语法完全相同，只有一点不同，xrevrange 是反向遍历的，必传参数：name，start=起始的id默认为最大 '+'，end=结尾的id默认为最小 '-'，选传参数：count，不传默认显示全部",
            notes = "xrevrange 命令和 xrange 命令语法完全相同，只有一点不同，xrevrange 是反向遍历的，必传参数：name，start=起始的id默认为最大 '+'，end=结尾的id默认为最小 '-'，选传参数：count，不传默认显示全部",
            response = Result.class)
    @PostMapping("/redis/xrevrange")
    public Result xrevrange(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String start = map.getStart();
        String end = map.getEnd();
        if (start==null || start.equals("string")){
            start="+";
        }
        if (end==null || end.equals("string")){
            end="-";
        }
        Long count = map.getCount();
        Range<String> stringRange = Range.create(start, end);
        //commands.xrange("queuetest",0,10);
        if (count != null && count > 0) {
            List<StreamMessage<String, String>> xrevrange = commands.RedisConnectionPublicClass()
                    .xrevrange(name, stringRange, Limit.from(count));
            if (xrevrange.size() == 0) {
                return Result.fail(
                        "fail：队列为空,请检查key和消息ID是否正确,\t" +
                        "或执行 /xinfoStream 查看全部信息,\t" +
                        " /xinfoGroup 查看群属, /xinfoConsumer 查看消费者,\t" +
                        "也可以执行 /xpending 和 /xpendinginfo 查看未读取的详细信息,\t" +
                        "之后在执行 /xreadgroup 查看消息,\t" +
                        "最后执行 /xack 查看确认的消息"
                );
            } else {
                return Result.ok("已返回key:" + name + "的消息数量"+xrevrange.size(), xrevrange);
//                        "success：已返回key:" + name + "的消息数量:" + xrevrange;
            }
        } else {
            List<StreamMessage<String, String>> xrevrange = commands.RedisConnectionPublicClass()
                    .xrevrange(name, stringRange);
            if (xrevrange.size() == 0) {
                return Result.fail(
                        "fail：队列为空,请检查key和消息ID是否正确,\t" +
                        "或执行/xinfoStream查看全部信息,\t" +
                        "/xinfoGroup查看群属,/xinfoConsumer查看消费者,\t" +
                        "也可以执行/xpending和/xpendinginfo查看未读取的详细信息,\t" +
                        "之后在执行/xreadgroup查看消息,\t" +
                        "最后执行/xack查看确认的消息"
                );
            } else {
                return Result.ok("已返回key:" + name + "的消息数量"+xrevrange.size(), xrevrange);
//                        "success：已返回key:" + name + "的消息数量:" + xrevrange;
            }
        }
    }

    //xclaim 命令用于更改未确认消息的所有权，如果有消费者在读取了消息之后未处理完成就挂掉了，那么消息会一直在 pending 队列中，占用内存，这时需要使用 xclaim 命令更改此条消息的所属者，让其他的消费者去消费这条消息。
    @SentinelResource(value = "xclaim")
    @ApiOperation(value = "xclaim 命令用于更改未确认消息的所有权，如果有消费者在读取了消息之后未处理完成就挂掉了，那么消息会一直在 pending 队列中，占用内存，这时需要使用 xclaim 命令更改此条消息的所属者，让其他的消费者去消费这条消息，必传参数：name，groupname，consumer，offset，retrycount，consumer为接收方，offset为发送方id，retrycount默认为0",
            notes = "xclaim 命令用于更改未确认消息的所有权，如果有消费者在读取了消息之后未处理完成就挂掉了，那么消息会一直在 pending 队列中，占用内存，这时需要使用 xclaim 命令更改此条消息的所属者，让其他的消费者去消费这条消息，必传参数：name，groupname，consumer，offset，retrycount，consumer为接收方，offset为发送方id，retrycount默认为0",
            response = Result.class)
    @PostMapping("/redis/xclaim")
    public Result xclaim(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String groupname = map.getGroupname();
        String consumer = map.getConsumer();
        String offset = map.getOffset();
        Long retrycount = map.getRetrycount();
        List<StreamMessage<String, String>> xclaim = commands.RedisConnectionPublicClass()
                .xclaim(name, Consumer.from(groupname, consumer), retrycount, offset);
        if (xclaim.size() == 0) {
            return Result.fail(
                                "fail：该消息已经被确认或消息有误,请检查key和消息ID是否正确,\t" +
                                        "或执行 /xinfoStream 查看全部信息,\t" +
                                        " /xinfoGroup 查看群属, /xinfoConsumer 查看消费者,\t" +
                                        "也可以执行 /xpending 和 /xpendinginfo 查看未读取的详细信息,\t" +
                                        "之后在执行 /xreadgroup 查看消息,\t" +
                                        "最后执行 /xack 查看确认的消息"
            );
        } else {
            return Result.ok("已返回key:" + name + "的消息数量", xclaim);
        }
    }

    //xtrim 命令会从 ID 值比较小的消息开始丢弃
    @SentinelResource(value = "xtrim")
    @ApiOperation(value = "xtrim 命令会从 ID 值比较小的消息开始丢弃，必传参数 name，count（为修剪后剩余的数量），选填参数 approxvalue='~'如果使用了 ~ 参数，则可能不会进行修剪。此参数告诉 redis 在能够删除整个宏节点时才执行修剪，这样做效率更高，并且可以保证消息的数量不小于所需要的数量。",
            notes = "xtrim 命令会从 ID 值比较小的消息开始丢弃，必传参数 name，count（为修剪后剩余的数量），选填参数 approxvalue='~'如果使用了 ~ 参数，则可能不会进行修剪。此参数告诉 redis 在能够删除整个宏节点时才执行修剪，这样做效率更高，并且可以保证消息的数量不小于所需要的数量。",
            response = Result.class)
    @PostMapping("/redis/xtrim")
    public Result xtrim(@RequestBody SomeObjiet map) {
        String name = map.getName();
        Long count = map.getCount();
        String approxvalue = map.getApproxvalue();

        if (approxvalue.equals("~")) {
            Long xtrim = commands.RedisConnectionPublicClass()
                    .xtrim(name, true, count);
            if (xtrim == 0) {
                return Result.fail(
                        "传入的参数有误，请先执行/xinfoStream查看全部信息,\t" +
                        "或/xinfoGroup查看群属,/xinfoConsumer查看消费者,\t" +
                        "也可以执行/xpending和/xpendinginfo查看未读取的详细信息,\t" +
                        "之后在执行/xreadgroup查看消息\t"
                );
            } else {
                return Result.ok("已返回key:" + name + "的消息修剪数量", xtrim);
//                        "success：已返回key:" + name + "的消息数量:" + xtrim;
            }
        }else {
            Long xtrim = commands.RedisConnectionPublicClass()
                    .xtrim(name, count);
            if (xtrim == 0) {
                return Result.fail(
                        "传入的参数有误，请先执行/xinfoStream查看全部信息,\t" +
                        "或/xinfoGroup查看群属,/xinfoConsumer查看消费者,\t" +
                        "也可以执行/xpending和/xpendinginfo查看未读取的详细信息,\t" +
                        "之后在执行/xreadgroup查看消息\t"
                );
            } else {
                return  Result.ok("已返回key:" + name + "的消息修剪数量", xtrim);
//                        "success：已返回key:" + name + "的消息数量:" + xtrim;
            }
        }
    }


        //使用 readgroup 命令读取最新的消息
    @SentinelResource(value = "xreadgroupRead")
    @ApiOperation(value = "测试版 -- 使用 readgroup 命令读取最新的消息", notes = "使用 readgroup 命令读取最新的消息")
    @PostMapping("/redis/xreadgroupRead")
    public List xreadgroupRead(@RequestBody SomeObjiet map) {
        String name = map.getName();
        String groupname = map.getGroupname();
        String consumer = map.getConsumer();
        String offset = map.getOffset();
        Long count = map.getCount();
        //XReadArgs count1 = new XReadArgs().count(count);
        //commands.readgroup("queuetest", "group1", "consumer1", "10");
        List<StreamMessage<String, String>> xreadgroup
                = commands.RedisConnectionPublicClass().xreadgroup(Consumer.from(groupname, consumer),
                XReadArgs.Builder.count(count),
                XReadArgs.StreamOffset.from(name, offset)
        );
        //System.out.println(xreadgroup.size());
                return xreadgroup;
        }

}

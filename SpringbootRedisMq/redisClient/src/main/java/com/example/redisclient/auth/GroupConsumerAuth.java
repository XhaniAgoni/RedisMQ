package com.example.redisclient.auth;

import com.example.redisclient.configuration.RedisConnectionPublic;
import com.example.redisclient.controller.ClientController;
import com.example.redisclient.entity.SomeObjiet;
import io.lettuce.core.Consumer;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class GroupConsumerAuth {
    @Autowired
    private RedisConnectionPublic commands;
    @Autowired
    private ClientController clientController;

    //验证群组是否存在，已存在即对消费者进行判断，若不存在则创建消费者
    public boolean checkGroup(SomeObjiet someObjiet) {
        String groupname = someObjiet.getGroupname();
        String consumer = someObjiet.getConsumer();
        String name = someObjiet.getName();
        String offset = someObjiet.getOffset();
        Long count = someObjiet.getCount();
        XReadArgs count1 = new XReadArgs().count(count);

        List list = (List) clientController.xinfoGroups(someObjiet).getData();
        //System.out.println("list:" + list);
        List list1 = (List) clientController.xinfoConsumers(someObjiet).getData();
        //System.out.println("list1:" + list1);
        String s = list.toString();
        //System.out.println("s:" + s);
        String s1 = list1.toString();
        //System.out.println("s1:" + s1);
        for (int i = 0; i < list.size(); i++) {
            String group = list.get(i).toString();
            //System.out.println("group:" + group);
            String substring = group.substring(1, group.length() - 1);
            List<String> strings = Arrays.asList(substring.split(","));
            String s3 = strings.get(1);
            String substring1 = s3.substring(1);
            //群组存在，且有消费者存在不为空
            if (substring1.equals(groupname) && list1.size() > 0) {
                for (int j = 0; j < list1.size(); j++) {
                    String consumers = list1.get(j).toString();
                    //System.out.println("consumer:" + consumers);
                    String substring01 = consumers.substring(1, consumers.length() - 1);
                    List<String> strings1 = Arrays.asList(substring01.split(","));
                    String s31 = strings1.get(1);
                    String substring11 = s31.substring(1);
                    if (substring11.equals(consumer)) {
                        return true;
                    } else if (j == list1.size() - 1) {
                        //群组存在，但消费者不存在,对消费者进行创建，创建消费者应当是offset为0，count为0,当offset为0时，count应为0，当offset为0时，count不论为多少都无意义且默认为0
                        List<StreamMessage<String, String>> xreadgroup = commands.RedisConnectionPublicClass()
                                .xreadgroup(Consumer.from(groupname, consumer),
                                        XReadArgs.Builder.count(0),
                                        XReadArgs.StreamOffset.from(name, "0"));
                        //System.out.println("xreadgroup查询群组消费者:" + xreadgroup);
                        //再去查询消费者是否存在
                        List list2 = (List) clientController.xinfoConsumers(someObjiet).getData();
                        //System.out.println("list2消费者:" + list2);
                        return true;

                    }
                }
            } else if (substring1.equals(groupname) && list1.size() == 0) {//虽有冗余，不足为虑
                //对新建的群组存在，消费者必不存在且为0，利用xreadgroup创建消费者，应当是offset为0，count为0；当offset为0时，count应为0，当offset为0时，count不论为多少都无意义且默认为0
                List<StreamMessage<String, String>> xreadgroup = commands.RedisConnectionPublicClass()
                        .xreadgroup(Consumer.from(groupname, consumer),
                                XReadArgs.Builder.count(0),
                                XReadArgs.StreamOffset.from(name, "0")
                        );
                //System.out.println("xreadgroup查询群组消费者:" + xreadgroup);
                //再去查询消费者是否存在
                List list2 = (List) clientController.xinfoConsumers(someObjiet).getData();
                //System.out.println("list2消费者:" + list2);
                return true;
            }
        }

        return false;
    }

    //验证群组是否存在，不存在则创建群组
    public boolean checkGroup1(SomeObjiet someObjiet) {
        String groupname = someObjiet.getGroupname();
        String consumer = someObjiet.getConsumer();
        String name = someObjiet.getName();
        List list = (List) clientController.xinfoGroups(someObjiet).getData();
        //System.out.println("list:" + list);
        String s = list.toString();
        //System.out.println("s:" + s);
        for (int i = 0; i < list.size(); i++) {
            String group = list.get(i).toString();
            //System.out.println("group:" + group);
            String substring = group.substring(1, group.length() - 1);
            List<String> strings = Arrays.asList(substring.split(","));
            String s3 = strings.get(1);
            String substring1 = s3.substring(1);
            if (substring1.equals(groupname)) {
                return true;

            } else if (i == list.size() - 1 && !substring1.equals(groupname)) {
                String s1 = (String) clientController.xgroupCreate(someObjiet).getData();
                //System.out.println(s1);
//                checkGroup(someObjiet);
                return true;
            }
        }
        if (list.size() == 0) {
            String s1 = (String) clientController.xgroupCreate(someObjiet).getData();
            //System.out.println(s1);
        }
        return true;
    }

    //验证消费者是否有pending状态，不为0则返回true，否则返回false
    public boolean checkConsumer(SomeObjiet someObjiet) {
        String groupname = someObjiet.getGroupname();
        String consumer = someObjiet.getConsumer();
        String name = someObjiet.getName();
        String offset = someObjiet.getOffset();
        List list = (List) clientController.xinfoConsumers(someObjiet).getData();
        //System.out.println("list:" + list);
        for (int i = 0; i < list.size(); i++) {
            String consumers = list.get(i).toString();
            //System.out.println("consumer:" + consumers);
            String substring = consumers.substring(1, consumers.length() - 1);
            List<String> strings = Arrays.asList(substring.split(","));
            String s3 = strings.get(1);
            String pendings3 = strings.get(3);
            String substring1 = s3.substring(1);
            String substring2 = pendings3.substring(1);
            if (substring1.equals(consumer) && !substring2.equals("0")) {
                return true;
            }

        }return false;
    }

    //验证删除的id是否有消费者正在消费，如果有则不能删除，如果没有则删除，但可以将该id强制删除
    public boolean checkDelete(SomeObjiet someObjiet) {
        String groupname;
        String consumer;
        String name = someObjiet.getName();
        String offset = someObjiet.getOffset();
        List list = (List) clientController.xinfoGroups(someObjiet).getData();
        for (int i=0;i<list.size();i++){
            String group = list.get(i).toString();
            //System.out.println("group:" + group);
            String substring = group.substring(1, group.length() - 1);
            List<String> strings = Arrays.asList(substring.split(","));
            String s3 = strings.get(1);
            groupname = s3.substring(1);
            someObjiet.setGroupname(groupname);
            List list1 = (List) clientController.xinfoConsumers(someObjiet).getData();
            for (int j=0;j<list1.size();j++){
                String consumers = list1.get(j).toString();
                //System.out.println("consumer:" + consumers);
                String substring1 = consumers.substring(1, consumers.length() - 1);
                List<String> strings1 = Arrays.asList(substring1.split(","));
                String s3_1 = strings1.get(1);
                consumer = s3_1.substring(1);
                someObjiet.setConsumer(consumer);
                someObjiet.setStart("-");
                someObjiet.setEnd("+");
                Long xlen = commands.RedisConnectionPublicClass().xlen(name);
                someObjiet.setCount(xlen);
                List xpendinginfo = (List) clientController.xpendinginfo(someObjiet).getData();
                for (int z=0;z<xpendinginfo.size();z++){
                    String s = xpendinginfo.get(z).toString();
                    String substring2 = s.substring(1, s.length() - 1);
                    List<String> strings2 = Arrays.asList(substring2.split(","));
                    String s3_2 = strings2.get(0);
                    if (s3_2.equals(offset)){
                        return false;
                    }
                }
            }

        }return true;
    }

    //验证群组是否存在 存在返回true 不存在返回false
    public boolean checkGroup2(SomeObjiet someObjiet) {
        String groupname = someObjiet.getGroupname();
        String consumer = someObjiet.getConsumer();
        String name = someObjiet.getName();
        String offset = someObjiet.getOffset();
        List list = (List) clientController.xinfoGroups(someObjiet).getData();
        //System.out.println("list:" + list);
        for (int i = 0; i < list.size(); i++) {
            String group = list.get(i).toString();
            //System.out.println("group:" + group);
            String substring = group.substring(1, group.length() - 1);
            List<String> strings = Arrays.asList(substring.split(","));
            String s3 = strings.get(1);
            String substring1 = s3.substring(1);
            if (substring1.equals(groupname)) {
                return true;
            }
        }
        return false;
    }

    //验证删除分组中是否有消费者正在消费，如果有则不能删除，如果没有则删除，但可以将该组强制删除，消费者也不会保留
    public boolean checkGroupDelete(SomeObjiet someObjiet) {
        String groupname;
        String consumer;
        String name = someObjiet.getName();
        String offset = someObjiet.getOffset();
        List data = (List) clientController.xpending(someObjiet).getData();
        String s = data.get(0).toString();
        if (s.equals("0")) {
            return true;
        }else {
            return false;
        }
    }
    //验证队列是否存在 存在返回true 不存在返回false
    public boolean checkQueue(SomeObjiet someObjiet) {
        String name = someObjiet.getName();
        List<String> keys = commands.RedisConnectionPublicClass().keys("*");
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).equals(name)) {
                return true;
            }
        }return false;
    }

}
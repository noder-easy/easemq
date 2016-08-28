package com.github.easynoder.easemq.commons;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public class ContextHelper {


    public static List<String> consumerList = new CopyOnWriteArrayList<String>();

    /**
     * topic对应的消费者
     */
    public static ConcurrentMap<String/*topic*/, String/*hostport*/> topicHostportMap = new ConcurrentHashMap<String, String>();

    /**
     * server端存储所有的
     */
    public static ConcurrentMap<String/*hostport*/, ChannelHandlerContext> ctxMap = new ConcurrentHashMap<String, ChannelHandlerContext>();

    public static void addConsumerAddr(String addr) {
        consumerList.add(addr);
    }

    public static void addTopicConsumer(String topic, String topicHostport) {
        topicHostportMap.put(topic, topicHostport);
        System.out.println("topicHostportMap = "+topicHostportMap);
    }

    public static void addCtx(String addr, ChannelHandlerContext ctx) {
        ctxMap.put(addr, ctx);
        System.out.println("addr list: " + ctxMap.keySet());
    }
}

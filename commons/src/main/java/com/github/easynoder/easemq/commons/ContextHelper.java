package com.github.easynoder.easemq.commons;

import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public class ContextHelper {


    public static Jedis jedis = new Jedis("localhost", 6379);

    /**
     * topic对应的消费者
     */
    public static ConcurrentMap<String/*topic*/, String/*hostport*/> topicHostportMap = new ConcurrentHashMap<String, String>();

    /**
     * server端存储所有的
     */
    public static ConcurrentMap<String/*hostport*/, ChannelHandlerContext> ctxMap = new ConcurrentHashMap<String, ChannelHandlerContext>();

    public static void addTopicConsumer(String topic, String topicHostport) {
        topicHostportMap.put(topic, topicHostport);
        jedis = new Jedis("localhost", 6379);
        jedis.rpush(topic, topicHostport);

        System.out.println("topicHostportMap = " + topicHostportMap);
    }

    public static void addCtx(String addr, ChannelHandlerContext ctx) {
        ctxMap.put(addr, ctx);
        System.out.println("addr list: " + ctxMap.keySet());
    }
}

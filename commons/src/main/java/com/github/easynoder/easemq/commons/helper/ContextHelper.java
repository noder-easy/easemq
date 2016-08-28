package com.github.easynoder.easemq.commons.helper;

import com.github.easynoder.easemq.commons.factory.JedisFactory;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Desc: 客户端消费者连接管理,后续将废弃redis,由zk来管理
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public class ContextHelper {


    public static Jedis jedis = JedisFactory.getJedis();

    /**
     * topic对应的消费者,可以通过zk保证数据的一致性
     */
    public static ConcurrentMap<String/*topic*/, String/*hostport*/> topicAddressMap = new ConcurrentHashMap<String, String>();

    /**
     * todo 消息的订阅关系可以通过zk完成
     *
     * @param topic
     * @param topicHostport
     */
    public static void addTopicConsumer(String topic, String topicHostport) {
        topicAddressMap.put(topic, topicHostport);
        // todo zk
        jedis = new Jedis("localhost", 6379);
        jedis.rpush(topic, topicHostport);
        jedis.close();
    }

}

package com.github.easynoder.easemq.server;

import com.github.easynoder.easemq.commons.ContextHelper;
import com.github.easynoder.easemq.core.exception.StoreException;
import com.github.easynoder.easemq.core.store.IStore;
import com.github.easynoder.easemq.core.store.memory.DirectMemoryStore;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Desc: 管理所有的serverclient,
 * Author:easynoder
 * Date:16/8/25
 * E-mail:easynoder@outlook.com
 */
public class NettyMQServerClientManager<T> implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyMQServerClientManager.class);

//    private ConcurrentMap<String/*topic*/, ChannelHandlerContext> ctxMap = new ConcurrentHashMap<String, ChannelHandlerContext>();

    private ConcurrentMap<String/*topic*/, IStore<T>> queueMap = new ConcurrentHashMap<String, IStore<T>>();

    public static Jedis jedis = new Jedis("localhost", 6379);


    private final Object QUEUE_LOCK = new Object();

    public synchronized void addData(String topic, T message) {
        try {

            /*if (queueMap.get(topic) == null) {
                synchronized (QUEUE_LOCK) {
                    if (queueMap.get(topic) == null) {
                        IStore<T> iStore = new DirectMemoryStore<T>(1000);
                        queueMap.put(topic, iStore);
                    }
                }
            }*/
            if (queueMap.get(topic) == null) {
                IStore<T> iStore = new DirectMemoryStore<T>(1000);
                queueMap.put(topic, iStore);
            }
            queueMap.get(topic).store(message);
        } catch (StoreException e) {
            e.printStackTrace();
        }
    }

/*    public synchronized void addCtx(String topic, ChannelHandlerContext ctx) {
        //ctxMap.replace(topic, ctx);
        if (ctxMap.get(topic) == null) {
            ctxMap.put(topic, ctx);
        }
    }*/

    public void run() {
        LOGGER.info("NettyMQServerClientManager starting...");

        try {
            Thread.sleep(10*1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (final Map.Entry<String, IStore<T>> entry : queueMap.entrySet()) {

            new Thread(new Runnable() {
                public void run() {
                    String topic = entry.getKey();
                    IStore<T> queue = entry.getValue();
                    while (true) {
                        T data = null;
                        try {
                            data = queue.get();
                        } catch (StoreException e) {
                            LOGGER.error("NettyMQServerClientManager send message error!", e);
                        }

                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("topic [{}] -> queue get message [{}]", topic, data);
                        }

//                        String addr = jedis.get(topic);
                        List<String> addrs = jedis.lrange(topic, 0, -1);
                        if (CollectionUtils.isEmpty(addrs)) {
                            LOGGER.warn("no consumer addr for topic = {}", topic);
                            continue;
                        }
//                        String addr = ContextHelper.topicHostportMap.get(topic);
                        String addr = addrs.get(RandomUtils.nextInt(addrs.size()));
                        if (StringUtils.isEmpty(addr)) {
                            LOGGER.warn("topic [{}] consumer is empty!", topic);
                            continue;
                        }
                        ChannelHandlerContext ctx = ContextHelper.ctxMap.get(addr);
                        if (ctx == null) {
                            LOGGER.warn("addr [{}] ctx is null!", addr);
                            continue;
                        }
                        if (ctx.channel().isActive()) {
                            ctx.channel().writeAndFlush(data);
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("addr -> [{}], topic -> [{}] , channel send message [{}] ok!", addr, topic, data);
                            }
                        } else {
                            LOGGER.warn("topic [{}] -> channel is inactive!", topic);
                        }
                    }
                }
            }).start();
        }

        try {
            Thread.sleep(1000 * 1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

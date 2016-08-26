package com.github.easynoder.easemq.server;

import com.github.easynoder.easemq.core.exception.StoreException;
import com.github.easynoder.easemq.core.store.IStore;
import com.github.easynoder.easemq.core.store.memory.DirectMemoryStore;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private ConcurrentMap<String/*topic*/, ChannelHandlerContext> ctxMap = new ConcurrentHashMap<String, ChannelHandlerContext>();

    private ConcurrentMap<String, IStore<T>> queueMap = new ConcurrentHashMap<String, IStore<T>>();

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

    public synchronized void addCtx(String topic, ChannelHandlerContext ctx) {
        //ctxMap.replace(topic, ctx);
        if (ctxMap.get(topic) == null) {
            ctxMap.put(topic, ctx);
        }
    }

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

                        ChannelHandlerContext ctx = ctxMap.get(topic);
                        if (ctx.channel().isActive()) {
                            ctx.channel().writeAndFlush(data);
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("topic [{}] -> channel send message [{}] ok!", topic, data);
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

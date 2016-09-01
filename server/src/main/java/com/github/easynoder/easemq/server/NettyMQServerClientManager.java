package com.github.easynoder.easemq.server;

import com.github.easynoder.easemq.commons.util.GsonUtils;
import com.github.easynoder.easemq.core.protocol.CmdType;
import com.github.easynoder.easemq.core.protocol.EasePacket;
import com.github.easynoder.easemq.core.protocol.EasePacketHeader;
import com.github.easynoder.easemq.core.protocol.GenerateMessage;
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
public class NettyMQServerClientManager implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyMQServerClientManager.class);

    /**
     * topic 对应的队列
     */
    private ConcurrentMap<String/*topic*/, IStore<EasePacket>> queueMap = new ConcurrentHashMap<String, IStore<EasePacket>>();

    /**
     * server端存储的所有的连接
     */
    private ConcurrentMap<String/*hostport*/, ChannelHandlerContext> ctxMap = new ConcurrentHashMap<String, ChannelHandlerContext>();

    public static Jedis jedis = new Jedis("localhost", 6379);

    private final Object QUEUE_LOCK = new Object();

    public synchronized void addMessage(String topic, EasePacket packet) {
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
                IStore<EasePacket> iStore = new DirectMemoryStore<EasePacket>(1000);
                queueMap.put(topic, iStore);
            }
            queueMap.get(topic).store(packet);
        } catch (StoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * 客户端连接上来时,立即存储该连接
     * todo thread-safe
     * @param addr
     * @param ctx
     */
    public void addCtx(String addr, ChannelHandlerContext ctx) {
        ctxMap.put(addr, ctx);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("addr list: " + ctxMap.keySet());
        }
    }

    public void run() {
        LOGGER.info("NettyMQServerClientManager starting >>>>>>>>>>>>>>>>>>>>>>");

        try {
            Thread.sleep(10*1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (final Map.Entry<String, IStore<EasePacket>> entry : queueMap.entrySet()) {

            new Thread(new Runnable() {
                public void run() {
                    String topic = entry.getKey();
                    IStore<EasePacket> queue = entry.getValue();
                    while (true) {
                        EasePacket data = null;
                        try {
                            data = queue.get();
                        } catch (StoreException e) {
                            LOGGER.error("NettyMQServerClientManager send message error!", e);
                        }

                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("topic [{}] -> queue get message [{}]", topic, data);
                        }

                        List<String> addrs = jedis.lrange(topic, 0, -1);
                        if (CollectionUtils.isEmpty(addrs)) {
                            LOGGER.warn("no listener addr for topic = {}", topic);
                            continue;
                        }
                        String addr = addrs.get(RandomUtils.nextInt(addrs.size()));
                        if (StringUtils.isEmpty(addr)) {
                            LOGGER.warn("topic [{}] listener is empty!", topic);
                            continue;
                        }
                        ChannelHandlerContext ctx = NettyMQServerClientManager.this.ctxMap.get(addr);
                        if (ctx == null) {
                            LOGGER.warn("addr [{}] ctx is null!", addr);
                            continue;
                        }
                        if (ctx.channel().isActive()) {

                            data.getMessage().getHeader().setTimestamp(System.currentTimeMillis());
                            ctx.channel().writeAndFlush(GsonUtils.getGson().toJson(data));
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

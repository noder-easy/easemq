package com.github.easynoder.easemq.server;

import com.github.easynoder.easemq.commons.ZkClient;
import com.github.easynoder.easemq.commons.factory.JedisFactory;
import com.github.easynoder.easemq.commons.util.GsonUtils;
import com.github.easynoder.easemq.core.exception.StoreException;
import com.github.easynoder.easemq.core.protocol.EasePacket;
import com.github.easynoder.easemq.core.store.IStore;
import com.github.easynoder.easemq.core.store.memory.DirectMemoryStore;
import com.github.easynoder.easemq.server.config.NettyMQConfig;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/2
 * E-mail:easynoder@outlook.com
 */
public class QueueServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueServer.class);

    private NettyMQConfig mqConfig;

    private ServerClientManager clientManager;

    /**
     * topic 对应的队列
     */
    private ConcurrentMap<String/*topic*/, IStore<EasePacket>> queueMap = new ConcurrentHashMap<String, IStore<EasePacket>>();

    // private static Jedis jedis = new Jedis("localhost", 6379);

    private ZkClient zkClient;

    public NettyMQConfig getMqConfig() {
        return mqConfig;
    }

    public ServerClientManager getClientManager() {
        return clientManager;
    }

    public QueueServer(NettyMQConfig mqConfig, ServerClientManager clientManager, ZkClient zkClient) {
        Assert.notNull(mqConfig, "queue mqconfig can't be null.");
        this.mqConfig = mqConfig;
        this.clientManager = clientManager;
        this.zkClient = zkClient;
    }

    public synchronized void start() {
        if (mqConfig == null || !mqConfig.hasTopic()) {
            LOGGER.warn("queue init FAIL, please assign at least 1 topic!");
            return;
        }

        for (String tmpTopic : mqConfig.getTopics()) {

            final String topic = tmpTopic;
            final IStore<EasePacket> queue = new DirectMemoryStore<EasePacket>(mqConfig.getQueueSize());
            queueMap.put(topic, queue);
            new Thread(new QueueTask(topic, queue)).start();
        }

    }

    public void addMessage(String topic, EasePacket packet) throws StoreException {
        try {
            if (queueMap.get(topic) == null) {
                throw new StoreException("queue not exist, please assign a queue for topic = " + topic);
            }
            queueMap.get(topic).store(packet);
        } catch (StoreException e) {
            LOGGER.error("store message error", e);
        }
    }

    class QueueTask implements Runnable {

        private String topic;
        private IStore<EasePacket> queue;
        private String subPath = "";

        public QueueTask(String topic, IStore<EasePacket> queue) {
            this.topic = topic;
            this.queue = queue;
            // "/topic/" + this.listener.getTopic() + "/sub";
            subPath = "/topic/" + topic + "/sub";
        }

        public void run() {
            while (true) {
                EasePacket data = null;
                try {
                    data = queue.get();
                } catch (StoreException e) {
                    LOGGER.error("get message error!", e);
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("topic [{}] -> queue get message [{}]", topic, data);
                }

                List<String> addrs = zkClient.getChilden(subPath);
                if (CollectionUtils.isEmpty(addrs)) {
                    LOGGER.warn("no listener addr for topic = {}", topic);
                    continue;
                }
                String addr = addrs.get(RandomUtils.nextInt(addrs.size()));
                if (StringUtils.isEmpty(addr)) {
                    LOGGER.warn("topic [{}] listener is empty!", topic);
                    continue;
                }
                ChannelHandlerContext ctx = QueueServer.this.clientManager.getCtx(addr);
                if (ctx == null) {
                    LOGGER.warn("addr [{}] ctx is null!", addr);
                    continue;
                }
                if (ctx.channel().isActive()) {

                    data.getMessage().getHeader().setTimestamp(System.currentTimeMillis());
                    ctx.channel().writeAndFlush(GsonUtils.getGson().toJson(data));
                    if (LOGGER.isDebugEnabled()) {
                        // LOGGER.debug("addr -> [{}], topic -> [{}] , channel send message [{}] ok!", addr, topic, data);
                    }
                } else {
                    LOGGER.warn("topic [{}] -> channel is inactive!", topic);
                }
            }
        }
    }

}

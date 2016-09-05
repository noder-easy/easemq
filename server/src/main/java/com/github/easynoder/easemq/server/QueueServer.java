package com.github.easynoder.easemq.server;

import com.github.easynoder.easemq.commons.ZkClient;
import com.github.easynoder.easemq.commons.util.GsonUtils;
import com.github.easynoder.easemq.core.exception.StoreException;
import com.github.easynoder.easemq.core.protocol.EasePacket;
import com.github.easynoder.easemq.core.store.IStore;
import com.github.easynoder.easemq.core.store.memory.DirectMemoryStore;
import com.github.easynoder.easemq.server.config.NettyMQConfig;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Desc: 队列消息分发器
 * Author:easynoder
 * Date:16/9/2
 * E-mail:easynoder@outlook.com
 */
public class QueueServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueServer.class);

    private NettyMQConfig mqConfig;

    private ServerClientManager clientManager;

    private static final String TOPIC_ROOT_PATH = "/topic";

    /**
     * topic 对应的队列
     */
    private ConcurrentMap<String/*topic*/, IStore<EasePacket>> queueMap = new ConcurrentHashMap<String, IStore<EasePacket>>();

    private ConcurrentMap<String/*topic*/, List<String>> consumerMap = new ConcurrentHashMap<String, List<String>>();

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

        // 启动消息队列
        for (String tmpTopic : mqConfig.getTopics()) {

            final String topic = tmpTopic;
            final IStore<EasePacket> queue = new DirectMemoryStore<EasePacket>(mqConfig.getQueueSize());
            queueMap.put(topic, queue);
            new Thread(new QueueTask(topic, queue)).start();
        }

        //订阅topic
        for (final String topic : mqConfig.getTopics()) {

            String topicPath = TOPIC_ROOT_PATH + "/" + topic;
            String consumerPath = topicPath + "/sub";
            String producerPath = topicPath + "/pub";
            byte[] defaultValue = "1".getBytes(Charset.forName("utf-8"));

            if (!zkClient.createNode(consumerPath, defaultValue)) {
                LOGGER.warn("create node error, path = {}", consumerPath);
            }
           /* if (!zkClient.createNode(producerPath, defaultValue)) {
                LOGGER.warn("create node error, path = {}", producerPath);
            }*/
            zkClient.getChildren(consumerPath, new MqWatcher(topic, new MQCallback() {
                public void callback(String topic, Object consumerList) {
                    updateConsumer(topic, (List<String>) consumerList);
                }
            }));
//            zkClient.getChildren(producerPath, new MqWatcher());
        }


    }

    /**
     * todo thread-safe  优化
     *
     * @param topic
     * @param consumerList
     */
    public synchronized void updateConsumer(String topic, List<String> consumerList) {
        List<String> originList = consumerMap.get(topic);

        if (CollectionUtils.isEmpty(consumerList)) {
            for (String addr : consumerMap.get(topic)) {
                clientManager.getCtx(addr).channel().close();
                clientManager.removeCtx(addr);
            }
            consumerMap.remove(topic);
            LOGGER.warn("remove all consumer, topic = {}", topic);
        } else {

            if (CollectionUtils.isNotEmpty(originList) && CollectionUtils.isNotEmpty(consumerList)) {
                List<String> removeConsumer = ListUtils.removeAll(originList, consumerList);
                if (CollectionUtils.isNotEmpty(removeConsumer)) {
                    for (String consumer : removeConsumer) {
                        clientManager.getCtx(consumer).channel().close();
                        clientManager.removeCtx(consumer);
                    }
                }
            }
        }

        consumerMap.put(topic, consumerList);
        LOGGER.info("updateConsumer {} -> {}", originList, consumerList);

    }

    interface MQCallback {
        public void callback(String topic, Object data);
    }

    class MqWatcher implements CuratorWatcher {

        private String topic;

        private MQCallback mqCallback;

        public MqWatcher(final String topic, MQCallback mqCallback) {
            this.topic = topic;
            this.mqCallback = mqCallback;
        }

        public void updateNotify(String topic, Object data, MQCallback callback) {
            LOGGER.info("zk变更通知回调,topic = {} , data = {}", topic, data);
            callback.callback(topic, data);
        }

        public void process(WatchedEvent watchedEvent) throws Exception {

            switch (watchedEvent.getType()) {
                case NodeChildrenChanged:
                    //重新注册watcher
                    List<String> data = zkClient.getChildren(watchedEvent.getPath(), this);
                    LOGGER.info("MqWatcher update path = {}, value = {}", watchedEvent.getPath(), data);
                    mqCallback.callback(topic, data);
                    break;
                case NodeCreated:
                    break;
                case NodeDeleted:
                    // TODO: 16/9/4
                    break;
            }
        }
    }

    public String getRandomConsumer(String topic) {
        List<String> consumerList = consumerMap.get(topic);
        if (CollectionUtils.isEmpty(consumerList)) {
            return null;
        }
        return consumerList.get(RandomUtils.nextInt(consumerList.size()));
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

                String addr = getRandomConsumer(topic);
                if (StringUtils.isEmpty(addr)) {
                    LOGGER.warn("topic [{}] consumer is empty!", topic);
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
//                         LOGGER.debug("addr -> [{}], topic -> [{}] , channel send message [{}] ok!", addr, topic, data);
                    }
                } else {
                    LOGGER.warn("topic [{}] -> channel is inactive!", topic);
                }
            }
        }
    }

}

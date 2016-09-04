package com.github.easynoder.easemq.client;

import com.github.easynoder.easemq.client.listener.MessageListenerAdapter;
import com.github.easynoder.easemq.commons.HostPort;
import com.github.easynoder.easemq.core.protocol.CmdType;
import com.github.easynoder.easemq.core.protocol.GenerateMessage;
import com.github.easynoder.easemq.core.protocol.HeartBeatMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Desc: 连接管理器
 * Author:easynoder
 * Date:16/7/10
 * E-mail:easynoder@outlook.com
 */
public class MQClientManager {

    private String topic;

    private static final Logger LOGGER = LoggerFactory.getLogger(MQClientManager.class);

//    private ZkClient zkClient;

    private List<HostPort> mqServerList = new CopyOnWriteArrayList<HostPort>();

    private MessageListenerAdapter listener;

    private ZkManager zkManager;


    private ConcurrentMap<String, List<IMQClient>> topic2Clients = new ConcurrentHashMap<String, List<IMQClient>>();

    public MQClientManager(String topic, MessageListenerAdapter listener, ZkManager zkManager) {
        try {
            this.topic = topic;
            this.listener = listener;
            this.zkManager = zkManager;
            this.topic2Clients.put(topic, new CopyOnWriteArrayList<IMQClient>());
            loadMQServer();
            initClient();
            //new LiveCheckServer().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMQServer() throws Exception {
        List<String> serverList = zkManager.pullMQServers("/servers", new MqWatcher(topic));
        updateMQServer(topic, serverList);
    }

    private void updateMQServer(String topic, List<String> serverList) throws Exception {
        if (CollectionUtils.isEmpty(serverList)) {
            throw new Exception("please assign at least 1 mqserver!");
        }
        for (String server : serverList) {
            HostPort hostPort = new HostPort(server.split(":")[0], Integer.valueOf(server.split(":")[1]));
            mqServerList.add(hostPort);
        }
        LOGGER.info("update mq-server succ! topic = {}, mqServerList = {}", topic, mqServerList);
    }

    // TODO: 16/9/4 可以进一步优化
    class MqWatcher implements CuratorWatcher {

        private String topic;

        public MqWatcher(final String topic) {
            this.topic = topic;
        }

        public void process(WatchedEvent watchedEvent) throws Exception {

            switch (watchedEvent.getType()) {
                case NodeChildrenChanged:
                    //重新注册watcher
                    List<String> data = zkManager.pullMQServers(watchedEvent.getPath(), this);
                    LOGGER.info("MqWatcher update path = {}, value = {}", watchedEvent.getPath(), data);
                    updateMQServer(topic, data);
                    break;
                case NodeCreated:
                    break;
                case NodeDeleted:
                    // TODO: 16/9/4
                    break;
            }
        }
    }

    // TODO: 16/9/3 thread-safe
    private void initClient() {
        if (CollectionUtils.isEmpty(mqServerList)) {
            LOGGER.warn("mq-server addr is empty!");
            return;
        }

        for (HostPort hostPort : mqServerList) {
            IMQClient client = new NettyMQClient(hostPort, listener, zkManager);
            List<IMQClient> clients = topic2Clients.get(topic);
            clients.add(client);
            topic2Clients.put(topic, clients);

            //地址上报
            zkManager.pushConsumer(topic, client.localAddress());
        }

        LOGGER.info("init clients succ! topic = {}, clients.size = {}", topic, topic2Clients.get(topic).size());
    }

    public IMQClient findClient(String topic) {
        // TODO: 16/9/3 随机优化,按照权重分配
        IMQClient client = topic2Clients.get(topic).get(RandomUtils.nextInt(topic2Clients.get(topic).size()));
        if (client == null) {
            LOGGER.warn("no client available! topic = {}", topic);
        }
        return client;
    }

    public void send(String topic, GenerateMessage message) {
        IMQClient client = findClient(topic);
        if (client != null) {
            // client.send(CmdType.CMD_STRING_MSG, message);
            client.sendAndGet(CmdType.CMD_STRING_MSG, message);
        }
    }

    public void close() {
        for (Map.Entry<String, List<IMQClient>> entry : topic2Clients.entrySet()) {
            String topic = entry.getKey();
            List<IMQClient> clients = entry.getValue();
            if (CollectionUtils.isNotEmpty(clients)) {
                for (IMQClient client : clients) {
                    client.close();
                    LOGGER.info("close client succ! topic = {}, client = {}", topic, client);
                }
            }
        }
    }


    public void callback(String topic, IMQClient client) {
        topic2Clients.get(topic).remove(client);
        //// TODO: 16/9/4  
    }

    /**
     * 连接存活检测
     */
    class LiveCheckServer {

        public void run() {
            LOGGER.info("live check start >>>>>>>>");
            for (final IMQClient client : topic2Clients.get(topic)) {
                // ping-pong 保持连接
                //client.sendAndGet()
                new Thread(new Runnable() {
                    public void run() {
                        int count = 0;
                        while (true) {
                            Object pong = client.sendAndGet(CmdType.CMD_HEARTBEAT, new HeartBeatMessage());
                            if (pong == null) {
                                try {
                                    Thread.sleep(2000 * count);
                                    count++;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (count >= 5) {
                                // 连接断开了.需要回调清理一下
                                callback(topic, client);
                            }
                        }
                    }
                }).start();
            }
        }
    }

}

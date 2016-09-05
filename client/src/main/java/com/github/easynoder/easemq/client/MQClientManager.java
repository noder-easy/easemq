package com.github.easynoder.easemq.client;

import com.github.easynoder.easemq.client.listener.MessageListenerAdapter;
import com.github.easynoder.easemq.commons.HostPort;
import com.github.easynoder.easemq.core.protocol.CmdType;
import com.github.easynoder.easemq.core.protocol.GenerateMessage;
import com.github.easynoder.easemq.core.protocol.HeartBeatMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
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

    private MessageListenerAdapter listener;

    private ZkManager zkManager;

    private List<HostPort> mqServerList = new CopyOnWriteArrayList<HostPort>();

    private ConcurrentMap<HostPort/*mq-server*/, List<IMQClient>> server2Clients = new ConcurrentHashMap<HostPort, List<IMQClient>>();

    public MQClientManager(String topic, MessageListenerAdapter listener, ZkManager zkManager) {
        try {
            this.topic = topic;
            this.listener = listener;
            this.zkManager = zkManager;
            loadMQServer();
//            initClient();
            //new LiveCheckServer().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMQServer() throws Exception {
        List<String> serverList = zkManager.pullMQServers("/servers", new MqWatcher(topic));
        if (CollectionUtils.isNotEmpty(serverList)) {
            for (String server : serverList) {
                HostPort hostPort = new HostPort(server.split(":")[0], Integer.valueOf(server.split(":")[1]));
                server2Clients.put(hostPort, new CopyOnWriteArrayList<IMQClient>());
            }
        }
        updateMQServer(topic, serverList);
    }

    private void updateMQServer(String topic, List<String> serverList) throws Exception {
        if (CollectionUtils.isEmpty(serverList)) {
            throw new Exception("please assign at least 1 mqserver!");
        }

        List<HostPort> updateMqServerList = new CopyOnWriteArrayList<HostPort>();
        for (String server : serverList) {
            HostPort hostPort = new HostPort(server.split(":")[0], Integer.valueOf(server.split(":")[1]));
            updateMqServerList.add(hostPort);
        }

        List<HostPort> targetMqServerList = new CopyOnWriteArrayList<HostPort>();

        // 需要关闭的连接
        List<HostPort> removeMqServerList = ListUtils.removeAll(mqServerList, updateMqServerList);

        if (CollectionUtils.isNotEmpty(removeMqServerList)) {
            for (HostPort server : removeMqServerList) {
                if (server2Clients.get(server) == null) {
                    LOGGER.info("clients not exists! hostport = {}", server);
                    continue;
                }
                for (IMQClient client : server2Clients.get(server)) {
                    client.close();
                }
                server2Clients.remove(server);
            }
            LOGGER.info("close clients succ! clients = {}", removeMqServerList);
        }

        targetMqServerList.addAll(ListUtils.removeAll(mqServerList, removeMqServerList));

        // 新增mqserver
        List<HostPort> addMqServerList = ListUtils.removeAll(updateMqServerList, mqServerList);
        if (CollectionUtils.isNotEmpty(addMqServerList)) {
            for (HostPort server : addMqServerList) {
                IMQClient client = createMQClient(server, listener, zkManager);
                LOGGER.info("创建连接成功! topic = {}, server = {}", topic, server);
                // todo thread-safe
                List list = server2Clients.get(server);
                if (list == null) {
                    list = new CopyOnWriteArrayList();
                }
                list.add(client);
                server2Clients.put(server, list);
                targetMqServerList.add(server);

                //上报地址
                if (listener != null) {
                    zkManager.pushConsumer(topic, client.localAddress());
                }
                LOGGER.info("add mq-server succ! topic = {}, server = {}, clients = {}", topic, server, list.size());
            }
        }

        mqServerList = targetMqServerList;
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

    private IMQClient createMQClient(HostPort server, MessageListenerAdapter listenerAdapter, ZkManager zkManager) {
        return new NettyMQClient(server, listener, zkManager);
    }

    public IMQClient findClient(String topic) {

        if (server2Clients.size() < 1) {
            throw new RuntimeException("no server for topic = " + topic);
        } else if (server2Clients.size() == 1) {
            int randomServer = RandomUtils.nextInt(server2Clients.keySet().size());
            List<IMQClient> clients = server2Clients.get(mqServerList.get(randomServer));
            IMQClient client = null;
            if (CollectionUtils.isNotEmpty(clients)) {
                if (clients.size() == 1) {
                    client = clients.get(0);
                } else {
                    client = clients.get(RandomUtils.nextInt(clients.size()));
                }
            }
            if (client == null) {
                LOGGER.warn("no client available! topic = {}", topic);
            }
            return client;
        } else {
            // TODO: 16/9/3 随机优化,按照权重分配
            int randomServer = RandomUtils.nextInt(server2Clients.keySet().size());
            IMQClient client = server2Clients.get(randomServer)
                    .get(RandomUtils.nextInt(server2Clients.get(randomServer).size()));
            if (client == null) {
                LOGGER.warn("no client available! topic = {}", topic);
            }
            return client;
        }

    }

    public void send(String topic, GenerateMessage message) {
        IMQClient client = findClient(topic);
        if (client != null) {
            // client.send(CmdType.CMD_STRING_MSG, message);
            client.sendAndGet(CmdType.CMD_STRING_MSG, message);
        }
    }

    public void close() {
        for (Map.Entry<HostPort, List<IMQClient>> entry : server2Clients.entrySet()) {
            HostPort server = entry.getKey();
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
       // topic2Clients.get(topic).remove(client);
        //// TODO: 16/9/4  
    }

    /**
     * 连接存活检测
     */
    class LiveCheckServer {

        public void run() {
            LOGGER.info("live check start >>>>>>>>");
            for (final IMQClient client : server2Clients.get(null)) {
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

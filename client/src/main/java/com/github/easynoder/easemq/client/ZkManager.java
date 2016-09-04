package com.github.easynoder.easemq.client;

import com.github.easynoder.easemq.commons.ZkClient;
import org.apache.curator.framework.api.CuratorWatcher;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Desc: zk客户端管理,zk api的封装, 可以作为Bean共享使用,
 * Author:easynoder
 * Date:16/9/4
 * E-mail:easynoder@outlook.com
 */
public class ZkManager {

    private String zkAddr = "localhost:2181";

    private ZkClient zkClient;

    public ZkManager() {
    }

    public void init() {
        zkClient = new ZkClient(this.zkAddr);
    }

    public void setZkAddr(String zkAddr) {
        this.zkAddr = zkAddr;
    }

    public String getZkAddr() {
        return zkAddr;
    }

    // 业务处理
    public void update() {

    }

    public List<String> pullMQServers(String path, CuratorWatcher watcher) {
        return this.zkClient.getChildren(path, watcher);
    }


    public boolean pushConsumer(String topic, String address) {
        // 将消费者地址注册到zk  /easemq/topic/${topic}/sub/${consumer1}...
        String path = "/topic/" + topic + "/sub/" + address;
        return this.zkClient.createNode(path, "1".getBytes(Charset.forName("utf-8")));
    }


}

package com.github.easynoder.easemq.commons;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/3
 * E-mail:easynoder@outlook.com
 */
public class ZkClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkClient.class);

    private CuratorFramework client;

    private String zkAddr = "localhost:2181";
    private String namespace = "easemq";

    // TODO: 16/9/3 参数需要优化
    public ZkClient(String zkAddr) {
        this.zkAddr = zkAddr;

        this.client = CuratorFrameworkFactory.builder()
                .connectString(zkAddr)
                .namespace(namespace)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                .connectionTimeoutMs(5000)
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                ZkClient.this.close();
            }
        }));
    }

    public boolean createNode(String path, byte[] data) {
        try {
            if (checkExists(path)) {
                deleteNode(path);
            }
            this.client.create().creatingParentsIfNeeded().forPath(path, data);
            return true;
        } catch (Exception e) {
            //LOGGER.error("createNode error, path = {}, data = {}", path, new String(data, Charset.forName("utf-8")), e);
            // TODO: 16/9/3 日志需要优化
            LOGGER.error("create node fail!", e);
            return false;
        }
    }

    public boolean createNode(String path, CreateMode createMode, byte[] data) {

        try {
            this.client.create().withMode(createMode).forPath(path, data);
            return true;
        } catch (Exception e) {
            //LOGGER.error("createNode error, path = {}, mode = {}, data = {}", path, createMode, new String(data, Charset.forName("utf-8")));
            LOGGER.error("createNode error", e);
            return false;
        }
    }


    public boolean checkExists(String path) {
        try {
            Stat stat = this.client.checkExists().forPath(path);
            return stat != null;
        } catch (Exception e) {
            LOGGER.error("checkExists error, path = {}", e);
            return false;
        }
    }

    public boolean deleteNode(String path) {
        try {
            this.client.delete().forPath(path);
            return true;
        } catch (Exception e) {
            LOGGER.error("deleteNode error", e);
            return false;
        }
    }

    public byte[] getData(String path) {
        try {
            return this.client.getData().forPath(path);
        } catch (Exception e) {
            LOGGER.error("getData error", e);
            return null;
        }
    }

    public List<String> getChildren(String path) {
        try {
            return this.client.getChildren().usingWatcher((CuratorWatcher) null).forPath(path);
        } catch (Exception e) {
            LOGGER.error("getChildren error", e);
            return Collections.EMPTY_LIST;
        }
    }

    public List<String> getChildren(String path, CuratorWatcher watcher) {
        try {
            return this.client.getChildren().usingWatcher(watcher).forPath(path);
        } catch (Exception e) {
            LOGGER.error("getChildren error", e);
            return Collections.EMPTY_LIST;
        }
    }

    public void start() {
        this.client.start();
    }

    public void close() {
        this.client.close();
    }


}

package com.github.easynoder.easemq.client;

import com.github.easynoder.easemq.client.listener.MessageListener;
import com.github.easynoder.easemq.client.listener.MessageListenerAdapter;
import com.github.easynoder.easemq.core.protocol.CmdType;
import com.github.easynoder.easemq.core.protocol.GenerateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Desc: 对外开放的api, 调用入口
 * Author:easynoder
 * Date:16/9/4
 * E-mail:easynoder@outlook.com
 */
public class EaseMQClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EaseMQClient.class);

    private String topic;

    private MessageListenerAdapter listener;

    private ZkManager zkManager;

    private MQClientManager clientManager;

    public EaseMQClient() {
    }

    public void init() {
        this.clientManager = new MQClientManager(this.topic, this.listener, this.zkManager);
    }

    public EaseMQClient setZkManager(ZkManager zkManager) {
        this.zkManager = zkManager;
        return this;
    }

    public MessageListener getListener() {
        return listener;
    }

    public EaseMQClient setListener(MessageListenerAdapter listener) {
        this.listener = listener;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public EaseMQClient setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public void start() {
        //do nothing
    }

    public void close() {
        this.clientManager.close();
    }

    public void send(String topic, GenerateMessage message) {
        this.clientManager.findClient(topic).send(CmdType.CMD_STRING_MSG, message);
    }

    public Object sendAndGet(byte cmdType, GenerateMessage message) {
        return null;
    }

    public String localAddress() {
        return null;
    }

    public String remoteAddress() {
        return null;
    }

}

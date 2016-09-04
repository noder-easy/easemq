package com.github.easynoder.easemq.client.test;

import com.github.easynoder.easemq.client.EaseMQClient;
import com.github.easynoder.easemq.client.IMQClient;
import com.github.easynoder.easemq.client.MQClientManager;
import com.github.easynoder.easemq.client.ZkManager;
import com.github.easynoder.easemq.client.listener.DefaultMessageListener;
import com.github.easynoder.easemq.client.listener.EaseMQMessageListener;
import com.github.easynoder.easemq.client.listener.MessageListener;
import com.github.easynoder.easemq.client.listener.MessageListenerAdapter;
import com.github.easynoder.easemq.core.protocol.GenerateMessage;

import java.util.UUID;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/24
 * E-mail:easynoder@outlook.com
 */

public class DefaultProducerTest {


    public static void main(String[] args) throws InterruptedException {

        startProducer(1);
    }

    public static void startProducer(int index ) {

        String zkAddr = "localhost:2181";
        ZkManager zkManager = new ZkManager();
        zkManager.setZkAddr(zkAddr);
        zkManager.init();

        String topic = "easemq1";
        MessageListenerAdapter listener = new EaseMQMessageListener(topic, 2, new DefaultMessageListener());
        EaseMQClient client = new EaseMQClient();
        client.setTopic(topic);
        client.setListener(listener);
        client.setZkManager(zkManager);
        client.init();


        for (int i = 0;i < 1;i ++) {
            GenerateMessage.Header header = new GenerateMessage.Header();
            header.setTopic(topic);
            header.setMessageId(UUID.randomUUID().toString());
            header.setTimestamp(System.currentTimeMillis());
            GenerateMessage message = new GenerateMessage();
            message.setBody("body"+i);
            message.setHeader(header);
            client.send(message.getHeader().getTopic(), message);
        }
    }

}

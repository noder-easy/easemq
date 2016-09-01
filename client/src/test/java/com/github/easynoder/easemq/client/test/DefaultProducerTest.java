package com.github.easynoder.easemq.client.test;

import com.github.easynoder.easemq.client.listener.MessageListener;
import com.github.easynoder.easemq.client.listener.DefaultMessageListener;
import com.github.easynoder.easemq.client.NettyMQClient;
import com.github.easynoder.easemq.client.producer.DefaultProducer;
import com.github.easynoder.easemq.client.producer.IProducer;
import com.github.easynoder.easemq.core.protocol.CmdType;
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
        startProducer();
        try {
            Thread.sleep(1000* 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void startProducer() {
        MessageListener listener = new DefaultMessageListener("easemq");
        IProducer producer = new DefaultProducer(new NettyMQClient(listener));
        for (int i = 0;i < 1;i ++) {
            GenerateMessage.Header header = new GenerateMessage.Header();
            header.setTopic("easemq");
            header.setMessageId(UUID.randomUUID().toString());
            header.setTimestamp(System.currentTimeMillis());
            GenerateMessage message = new GenerateMessage();
            message.setBody("body"+i);
            message.setHeader(header);
            producer.send(message.getHeader().getTopic(), message);
        }

        System.out.println("start producer succ!");
    }

}

package com.github.easynoder.easemq.client.test;

import com.github.easynoder.easemq.client.IMQClient;
import com.github.easynoder.easemq.client.listener.MessageListener;
import com.github.easynoder.easemq.client.listener.DefaultMessageListener;
import com.github.easynoder.easemq.client.NettyMQClient;
import com.github.easynoder.easemq.client.producer.DefaultProducer;
import com.github.easynoder.easemq.client.producer.IProducer;
import com.github.easynoder.easemq.core.Message;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/24
 * E-mail:easynoder@outlook.com
 */

public class DefaultProducerTest {


    public static void main(String[] args) throws InterruptedException {
        startConsumer();
        startProducer();
        try {
            Thread.sleep(1000* 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void startProducer() {
        IProducer producer = new DefaultProducer(new NettyMQClient());
        for (int i = 0;i < 10;i ++) {
            Message.Header header = new Message.Header().setVersion(1).setExtra(0);
            Message message = new Message().setTopic("easemq" ).setBody("body"+i).setHeader(header);
            producer.send(message.getTopic(), message);
        }

        System.out.println("start producer succ!");
    }

    public static void startConsumer(){
        MessageListener listener = new DefaultMessageListener("easemq");
        IMQClient client = new NettyMQClient(listener);
        System.out.println("start consumer1 succ!");


        MessageListener listener1 = new DefaultMessageListener("easemq");
        IMQClient client1 = new NettyMQClient(listener1);
        System.out.println("start consumer2 succ!");

    }


}

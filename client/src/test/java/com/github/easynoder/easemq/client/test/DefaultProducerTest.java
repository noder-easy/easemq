package com.github.easynoder.easemq.client.test;

import com.github.easynoder.easemq.client.netty.NettyMQClient;
import com.github.easynoder.easemq.client.producer.DefaultProducer;
import com.github.easynoder.easemq.client.producer.IProducer;
import com.github.easynoder.easemq.core.Message;
import org.junit.Test;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/24
 * E-mail:easynoder@outlook.com
 */

public class DefaultProducerTest {

    @Test
    public void testSend() {
        IProducer producer = new DefaultProducer(new NettyMQClient());
        long topicNum = System.currentTimeMillis()/1000;
        for (int i = 0;i < 10;i ++) {
            Message.Header header = new Message.Header().setVersion(1).setExtra(0);
            Message message = new Message().setTopic("easemq-"+ topicNum ).setBody("body"+i).setHeader(header);
            producer.send(message.getTopic(), message);
        }

        System.out.println("send succ!");
        try {
            Thread.sleep(10* 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

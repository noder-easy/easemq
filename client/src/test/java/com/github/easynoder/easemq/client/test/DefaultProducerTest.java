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
        Message message = new Message().setBody("body").setHead("head");
        producer.send("testTopic", message);
        System.out.println("send succ!");
        try {
            Thread.sleep(10* 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

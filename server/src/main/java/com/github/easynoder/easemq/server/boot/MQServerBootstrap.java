package com.github.easynoder.easemq.server.boot;


import com.github.easynoder.easemq.commons.factory.JedisFactory;
import com.github.easynoder.easemq.server.QueueServer;
import com.github.easynoder.easemq.server.ServerClientManager;
import com.github.easynoder.easemq.server.config.NettyMQConfig;
import com.github.easynoder.easemq.server.exception.ConfigParseException;
import com.github.easynoder.easemq.server.netty.NettyMQServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/1
 * E-mail:easynoder@outlook.com
 */
public class MQServerBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(MQServerBootstrap.class);


    /**
     * usage java -Dtopic:mytopic1,mytopic2 -DqueueSize:10000
     *
     * @param args
     */
    public static void main(String[] args) {

        /*String dynamicTopics = System.getProperty("topic");
        if (StringUtils.isEmpty(dynamicTopics)) {
            LOGGER.info("需要指定监听的topic,启动失败.");
            LOGGER.info("Usage: java -Dtopic:mytopic1,mytopic2 ");
            return;
        }*/

        System.setProperty("topic", "easemq1,easemq2");
        NettyMQConfig config = new NettyMQConfig();
        try {
            config.loadFromJvm();
        } catch (ConfigParseException e) {
            e.printStackTrace();
            return;
        }
        new MQServerBootstrap().boot(config);
    }


    public void boot(NettyMQConfig mqConfig) {
        LOGGER.info("start netty mq-server >>>>>>>>>>>>>>>>>>>>");
        try {
            // TODO: 16/9/1 即将废弃
            /*JedisFactory.getJedis().del("easemq1");
            JedisFactory.getJedis().del("easemq2");*/
            new NettyMQServer(mqConfig).start();

            // TODO: 16/9/1 redis 作为topic的共享 即将废弃
           // JedisFactory.getJedis().set("topic", mqConfig.getTopics().toString());
            LOGGER.info("netty mq-server start succ! welcome!");
        } catch (InterruptedException e) {
            LOGGER.error("start netty server fail", e);
        }
    }
}

package com.github.easynoder.easemq.server.boot;


import com.github.easynoder.easemq.commons.factory.JedisFactory;
import com.github.easynoder.easemq.server.config.NettyMQConfig;
import com.github.easynoder.easemq.server.exception.ConfigParseException;
import com.github.easynoder.easemq.server.netty.NettyMQServer;
import org.apache.commons.lang3.StringUtils;
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
     * usage java -Dtopic:mytopic1,mytopic2
     * @param args
     */
    public static void main(String[] args) {

        String dynamicTopics = System.getProperty("topic");
        if (StringUtils.isEmpty(dynamicTopics)) {
            LOGGER.info("需要指定监听的topic,启动失败.");
            LOGGER.info("Usage: java -Dtopic:mytopic1,mytopic2 ");
            return;
        }

        NettyMQConfig config = new NettyMQConfig();
        try {
            config.loadFromDynamicParams(dynamicTopics);
        } catch (ConfigParseException e) {
            e.printStackTrace();
            return;
        }
        new MQServerBootstrap().boot(config);
    }


    public void boot(NettyMQConfig config) {
        LOGGER.info("start netty mq-server >>>>>>>>>>>>>>>>>>>>");
        try {
            // TODO: 16/9/1 即将废弃
            JedisFactory.getJedis().del("easemq");
            new NettyMQServer(config).start();
            // TODO: 16/9/1 redis 作为topic的共享 即将废弃
            JedisFactory.getJedis().set("topic", config.getTopics().toString());
            LOGGER.info("netty mq-server start succ! welcome!");
        } catch (InterruptedException e) {
            System.out.printf("start netty server fail");
            e.printStackTrace();
        }
    }
}

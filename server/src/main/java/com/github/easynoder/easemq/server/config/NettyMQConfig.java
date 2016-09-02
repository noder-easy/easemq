package com.github.easynoder.easemq.server.config;

import com.github.easynoder.easemq.server.ConfigUtils;
import com.github.easynoder.easemq.server.exception.ConfigParseException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/1
 * E-mail:easynoder@outlook.com
 */
public class NettyMQConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyMQConfig.class);

    public static final String DEFAULT_SPLITTER = ",";

    private static final int DEFAULT_QUEUE_SIZE = 100000;

    private List<String> topics;

    private int queueSize;

    public List<String> getTopics() {
        return topics;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public boolean hasTopic() {
        return CollectionUtils.isNotEmpty(topics);
    }

    public synchronized void loadFromFile(String filePath) throws ConfigParseException {
        // TODO: 16/9/1 从文件中读取topic
    }

    public synchronized void loadFromJvm() throws ConfigParseException {
        String topicConfig = ConfigUtils.getStringValue("topic","");
        // 从jvm启动参数中获取
        if (StringUtils.isEmpty(topicConfig)) {
            throw new ConfigParseException("需要指定推送消息的topic");
        }
        String[] topicList = StringUtils.split(topicConfig, DEFAULT_SPLITTER);
        // TODO: 16/9/1 校验参数的正确性
        this.topics = Arrays.asList(topicList);
        this.queueSize = ConfigUtils.getIntValue("queueSize", DEFAULT_QUEUE_SIZE);

        LOGGER.info("mq-config = {}", this);
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NettyMQConfig{");
        sb.append("queueSize=").append(queueSize);
        sb.append(", topics=").append(topics);
        sb.append('}');
        return sb.toString();
    }
}

package com.github.easynoder.easemq.server.config;

import com.github.easynoder.easemq.server.exception.ConfigParseException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/1
 * E-mail:easynoder@outlook.com
 */
public class NettyMQConfig {

    public static final String DEFAULT_SPLITTER = ",";

    private List<String> topics;

    public List<String> getTopics() {
        return topics;
    }

    public void loadFromFile(String filePath) throws ConfigParseException {
        // TODO: 16/9/1 从文件中读取topic
    }

    public void loadFromDynamicParams(String dynamicParams) throws ConfigParseException {
        // 从jvm启动参数中获取
        if (StringUtils.isEmpty(dynamicParams)) {
            throw new ConfigParseException("需要指定推送消息的topic");
        }
        String[] topicList = StringUtils.split(dynamicParams, DEFAULT_SPLITTER);
        // TODO: 16/9/1 校验参数的正确性
        this.topics = Arrays.asList(topicList);
    }


}

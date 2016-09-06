package com.github.easynoder.easemq.server;

import com.github.easynoder.easemq.core.protocol.EasePacket;
import com.github.easynoder.easemq.core.protocol.GenerateMessage;
import com.github.easynoder.easemq.core.store.IStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Desc: direct模式
 * Author:easynoder
 * Date:16/9/6
 * E-mail:easynoder@outlook.com
 */
public class DirectExchange implements Exchange {

    private String routingKey;

    private Map<String, IStore<EasePacket>> store = new ConcurrentHashMap<String, IStore<EasePacket>>();

    public void exchange(String routingKey, GenerateMessage message) {
        // TODO: 16/9/6
    }
}

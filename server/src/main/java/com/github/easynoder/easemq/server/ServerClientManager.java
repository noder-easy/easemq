package com.github.easynoder.easemq.server;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Desc: 管理所有的serverclient,
 * Author:easynoder
 * Date:16/8/25
 * E-mail:easynoder@outlook.com
 */
public class ServerClientManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerClientManager.class);

    /**
     * server端存储的所有的连接
     */
    public ConcurrentMap<String/*hostport*/, ChannelHandlerContext> ctxMap = new ConcurrentHashMap<String, ChannelHandlerContext>();

    /**
     * 客户端连接上来时,立即存储该连接
     * todo thread-safe
     *
     * @param addr
     * @param ctx
     */
    public void addCtx(String addr, ChannelHandlerContext ctx) {
        ctxMap.put(addr, ctx);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("addr list: " + ctxMap.keySet());
        }
    }


    /**
     * todo thread-safe
     *
     * @param addr
     * @return
     */
    public ChannelHandlerContext getCtx(String addr) {
        return ctxMap.get(addr);
    }

}

package com.github.easynoder.easemq.server.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.easynoder.easemq.core.Message;
import com.github.easynoder.easemq.core.store.IStore;
import com.github.easynoder.easemq.core.store.memory.DirectMemoryStore;
import com.github.easynoder.easemq.server.NettyMQServerClientManager;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Desc:
 * Author:easynoder
 * Date:16/7/24
 * E-mail:easynoder@outlook.com
 */
public class TcpServerHandler extends ChannelInboundHandlerAdapter {


    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServerHandler.class);

    private Gson gson = new Gson();

    private NettyMQServerClientManager clientManager ;

    public TcpServerHandler(NettyMQServerClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server channelActive>>>>>>>>"+ new Date());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Message message = gson.fromJson((String)msg, Message.class);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("server received msg {} ", message);
        }
        clientManager.addCtx(message.getTopic(), ctx);
        clientManager.addData(message.getTopic(), message.getBody());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exception caught...");
        cause.printStackTrace();
        ctx.close();
    }

}

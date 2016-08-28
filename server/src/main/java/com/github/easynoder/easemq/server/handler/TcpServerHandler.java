package com.github.easynoder.easemq.server.handler;

import com.github.easynoder.easemq.commons.util.GsonUtils;
import com.github.easynoder.easemq.core.Message;
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

    private NettyMQServerClientManager clientManager ;

    public TcpServerHandler(NettyMQServerClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("server channelActive>>>>>>>>"+ new Date());
        clientManager.addCtx(ctx.channel().remoteAddress().toString(), ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Message message = GsonUtils.getGson().fromJson((String)msg, Message.class);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("server received msg {} ", message);
        }
        clientManager.addMessage(message.getTopic(), message);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("exception caught", cause);
        ctx.close();
    }

}

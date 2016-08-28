package com.github.easynoder.easemq.client.handler;

import com.github.easynoder.easemq.client.MessageListener;
import com.github.easynoder.easemq.commons.ContextHelper;
import com.github.easynoder.easemq.core.Message;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Desc:
 * Author:easynoder
 * Date:16/7/24
 * E-mail:easynoder@outlook.com
 */


public class TcpClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpClientHandler.class);

    private Gson gson = new Gson();

    int count = 0;

    private MessageListener listener;

    public TcpClientHandler(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //ctx.writeAndFlush("hi server, how are you?" + count++);
        if (listener != null) {
            // consumer 记录消费者ip:端口
            LOGGER.info("registe consumer addr: {}", ctx.channel().localAddress().toString());
//            ContextHelper.addConsumerAddr(ctx.channel().localAddress().toString());
            ContextHelper.addTopicConsumer(listener.getTopic(), ctx.channel().localAddress().toString());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("client = {} receive message = {} ", ctx.channel().localAddress().toString(), msg);
        Message message = gson.fromJson((String) msg, Message.class);
        if (listener != null) {
            listener.onMessage(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        LOGGER.error("exception: ", cause);
    }
}

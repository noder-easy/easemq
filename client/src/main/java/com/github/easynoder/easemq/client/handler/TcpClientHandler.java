package com.github.easynoder.easemq.client.handler;

import com.github.easynoder.easemq.client.listener.MessageListener;
import com.github.easynoder.easemq.commons.helper.ContextHelper;
import com.github.easynoder.easemq.commons.util.GsonUtils;
import com.github.easynoder.easemq.core.AckUtils;
import com.github.easynoder.easemq.core.protocol.CmdType;
import com.github.easynoder.easemq.core.protocol.EasePacket;
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

    private MessageListener listener;

    public TcpClientHandler(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (listener != null) {
            // listener 记录消费者ip:端口
            LOGGER.info("topic = {}, registe listener addr = {}", listener.getTopic(), ctx.channel().localAddress().toString());
            ContextHelper.addTopicConsumer(listener.getTopic(), ctx.channel().localAddress().toString());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("client = {}, topic = {}, receive message = {} ", ctx.channel().localAddress().toString(), listener.getTopic(), msg);
        }
        EasePacket packet = GsonUtils.getGson().fromJson((String) msg, EasePacket.class);
        if (packet.getHeader().getCmdType() == CmdType.CMD_ACK) {
            LOGGER.info("client receive ack = {}", packet);
        } else {
            if (listener != null) {
                listener.onMessage(packet.getMessage());
            }
            // 客户端正常消费完后 ack 回执
            EasePacket ackPacket = AckUtils.buildAckPacket(packet.getMessage().getHeader(), true);
            ctx.writeAndFlush(GsonUtils.getGson().toJson(ackPacket));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("exception: ", cause);
        ctx.close();
    }
}

package com.github.easynoder.easemq.server.handler;

import com.github.easynoder.easemq.commons.util.GsonUtils;
import com.github.easynoder.easemq.core.AckUtils;
import com.github.easynoder.easemq.core.protocol.CmdType;
import com.github.easynoder.easemq.core.protocol.EasePacket;
import com.github.easynoder.easemq.server.QueueServer;
import com.github.easynoder.easemq.server.ServerClientManager;
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
public class EaseMQServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EaseMQServerHandler.class);

    private QueueServer queueServer;

    public EaseMQServerHandler(QueueServer queueServer) {
        this.queueServer = queueServer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("server channelActive>>>>>>>>"+ new Date());
        queueServer.getClientManager().addCtx(ctx.channel().remoteAddress().toString(), ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        EasePacket packet = GsonUtils.getGson().fromJson((String)msg, EasePacket.class);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("server received msg {} ", packet);
        }
        if (packet.getHeader().getCmdType() == CmdType.CMD_ACK) {
             // TODO: 16/8/29 其他类型的消息处理
            LOGGER.info("SEVER receive ack = {}", packet);
        } else {
            boolean succ = true;
            try{
                queueServer.addMessage(packet.getMessage().getHeader().getTopic(), packet);

            }catch (Exception e) {
                LOGGER.error("MQ Server store message FAIL", e);
                succ = false;
            }
            //服务端收到消息 ack回执
            EasePacket response = AckUtils.buildAckPacket(packet, packet.getMessage().getHeader(), succ);
            LOGGER.info("send ack = {}", GsonUtils.getGson().toJson(response));
            ctx.writeAndFlush(GsonUtils.getGson().toJson(response));
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("exception caught", cause);
        ctx.close();
    }

}

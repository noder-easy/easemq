package com.github.easynoder.easemq.server.handler;

import com.github.easynoder.easemq.commons.util.GsonUtils;
import com.github.easynoder.easemq.core.AckUtils;
import com.github.easynoder.easemq.core.protocol.CmdType;
import com.github.easynoder.easemq.core.protocol.EasePacket;
import com.github.easynoder.easemq.server.NettyMQServerClientManager;
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

        EasePacket packet = GsonUtils.getGson().fromJson((String)msg, EasePacket.class);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("server received msg {} ", packet);
        }
        if (packet.getHeader().getCmdType() == CmdType.CMD_ACK) {
             // TODO: 16/8/29
            LOGGER.info("SEVER receive ack = {}", packet);
        } else {
            // TODO: 16/8/29
            clientManager.addMessage(packet.getMessage().getHeader().getTopic(), packet.getMessage());
            //服务端收到消息 ack回执
            EasePacket response = AckUtils.buildAckPacket(packet.getMessage().getHeader(), true);
            ctx.writeAndFlush(GsonUtils.getGson().toJson(response));

        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("exception caught", cause);
        ctx.close();
    }

}

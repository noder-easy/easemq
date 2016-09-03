package com.github.easynoder.easemq.client.handler;

import com.github.easynoder.easemq.client.listener.MessageListener;
import com.github.easynoder.easemq.commons.ZkClient;
import com.github.easynoder.easemq.commons.helper.ContextHelper;
import com.github.easynoder.easemq.commons.util.GsonUtils;
import com.github.easynoder.easemq.core.AckUtils;
import com.github.easynoder.easemq.core.protocol.CmdType;
import com.github.easynoder.easemq.core.protocol.EasePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;


/**
 * Desc:
 * Author:easynoder
 * Date:16/7/24
 * E-mail:easynoder@outlook.com
 */
public class EaseMQClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EaseMQClientHandler.class);

    private MessageListener listener;

    private ZkClient zkClient;

    public EaseMQClientHandler(MessageListener listener, ZkClient zkClient) {
        this.listener = listener;
        this.zkClient = zkClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (listener != null) {
            // listener 记录消费者ip:端口
            LOGGER.info("topic = {}, registe listener addr = {}", listener.getTopic(), ctx.channel().localAddress().toString());
//            ContextHelper.addTopicConsumer(listener.getTopic(), ctx.channel().localAddress().toString());

            String addr = ctx.channel().localAddress().toString().substring(1);
            // 将消费者地址注册到zk  /easemq/topic/${topic}/sub/${consumer1}...
            String path = "/topic/" + this.listener.getTopic() + "/sub/" + addr;
            zkClient.createNode(path, "1".getBytes(Charset.forName("utf-8")));
            LOGGER.info("消费者注册到zk成功, topic = {}, path = {}", listener.getTopic(), path);
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
                boolean succ = true;
                try {
                    listener.onMessage(packet.getMessage());
                } catch (Exception e) {
                    LOGGER.error("MQ Client receive message FAIL", e);
                    succ = false;
                }
                // 客户端正常消费完后 ack 回执
                EasePacket ackPacket = AckUtils.buildAckPacket(packet, packet.getMessage().getHeader(), succ);
                ctx.writeAndFlush(GsonUtils.getGson().toJson(ackPacket));
            }

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("exception: ", cause);
        ctx.close();
    }
}

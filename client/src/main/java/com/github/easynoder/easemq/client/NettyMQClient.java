package com.github.easynoder.easemq.client;

import com.github.easynoder.easemq.client.handler.EaseMQClientHandler;
import com.github.easynoder.easemq.client.listener.MessageListenerAdapter;
import com.github.easynoder.easemq.commons.HostPort;
import com.github.easynoder.easemq.commons.util.GsonUtils;
import com.github.easynoder.easemq.core.protocol.AckMessage;
import com.github.easynoder.easemq.core.protocol.EasePacket;
import com.github.easynoder.easemq.core.protocol.EasePacketHeader;
import com.github.easynoder.easemq.core.protocol.GenerateMessage;
import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class NettyMQClient implements IMQClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyMQClient.class);

    private ChannelFuture channelFuture;

    private EventLoopGroup group = new NioEventLoopGroup();

    private Gson gson = GsonUtils.getGson();

    private HostPort hostPort;

    private MessageListenerAdapter listenerAdapter;

    private ZkManager zkManager;

    public NettyMQClient(HostPort hostPort, MessageListenerAdapter listenerAdapter, ZkManager zkManager) {
        this.hostPort = hostPort;
        this.listenerAdapter = listenerAdapter;
        this.zkManager = zkManager;
        this.start();
    }


    public void start() {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                    pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                    pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

                    pipeline.addLast("handler", new EaseMQClientHandler(listenerAdapter, zkManager));
                }
            });

            channelFuture = b.connect(this.hostPort.getHost(), this.hostPort.getPort()).sync();
            LOGGER.info("Netty client connected! hostport: {}", this.hostPort);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param cmdType
     * @param message
     */
    public void send(final byte cmdType, final GenerateMessage message) {
        EasePacketHeader packetHeader = new EasePacketHeader(cmdType);
        EasePacket packet = new EasePacket().setHeader(packetHeader).setMessage(message);
        Channel channel = channelFuture.channel();
        channel.write(gson.toJson(packet));

        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    // TODO: 16/8/30 统计
                } else {
                    LOGGER.warn("send error cmdType = {}, message = {}", cmdType, message);
                }
            }
        });

        // TODO: 16/8/30 异步优化
        channel.flush();
    }


    public Object sendAndGet(final byte cmdType, final GenerateMessage message) {
        EasePacketHeader packetHeader = new EasePacketHeader(cmdType);

        ResponseFuture responseFuture = new ResponseFuture(packetHeader.getOpaque());
        ResponseManager.addResponseFuture(packetHeader.getOpaque(), responseFuture);

        EasePacket packet = new EasePacket().setHeader(packetHeader).setMessage(message);
        Channel channel = channelFuture.channel();
        channel.write(gson.toJson(packet));

        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    // TODO: 16/8/30 统计
                } else {
                    LOGGER.warn("send error cmdType = {}, message = {}", cmdType, message);
                    System.out.println();
                }
            }
        });

        // TODO: 16/8/30 异步优化
        channel.flush();
        try {
            EasePacket easePacket = (EasePacket) responseFuture.get(1, TimeUnit.SECONDS);
            AckMessage ack = (AckMessage) easePacket.getMessage();
            if (ack.isSuccess()) {
                System.out.println("异步返回结果:" + true);
            } else{
                System.out.println("异步返回结果:" + false + "; 准备重投");
            }
            return ack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String localAddress() {
        return channelFuture.channel().localAddress().toString().substring(1);
    }

    public String remoteAddress() {
        return channelFuture.channel().localAddress().toString().substring(1);
    }

    public void close() {
        try {
            channelFuture.channel().closeFuture().sync();
            group.shutdownGracefully();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
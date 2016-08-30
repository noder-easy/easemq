package com.github.easynoder.easemq.client;

import com.github.easynoder.easemq.client.listener.MessageListener;
import com.github.easynoder.easemq.client.handler.TcpClientHandler;
import com.github.easynoder.easemq.commons.HostPort;
import com.github.easynoder.easemq.commons.util.GsonUtils;
import com.github.easynoder.easemq.core.protocol.EasePacket;
import com.github.easynoder.easemq.core.protocol.EasePacketHeader;
import com.github.easynoder.easemq.core.protocol.Message;
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
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyMQClient implements IMQClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyMQClient.class);

    private ChannelFuture channelFuture;

    private EventLoopGroup group = new NioEventLoopGroup();

    private Gson gson = GsonUtils.getGson();

    private HostPort hostPort;

    private MessageListener listener;

    public NettyMQClient(MessageListener listener) {
        this(new HostPort(), listener);
    }

    public NettyMQClient(HostPort hostPort, MessageListener listener) {
        this.hostPort = hostPort;
        this.listener = listener;
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

                    pipeline.addLast("handler", new TcpClientHandler(listener));
                }
            });

            channelFuture = b.connect(this.hostPort.getHost(), this.hostPort.getPort()).sync();
            LOGGER.info("Netty client connected! hostport: {}", this.hostPort);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    public void send(String topic, Message message) {
      *//*  Channel channel = channelFuture.channel();
        channel.writeAndFlush(gson.toJson(message));*//*
        send(topic, message, null);
    }*/

    /**
     * 发送
     *
     * @param topic
     * @param message
     */
    public void send(final String topic, final Message message) {
        EasePacketHeader packetHeader = new EasePacketHeader().setTopic(topic).setCmdType(message.getHeader().getCmdType());
        EasePacket packet = new EasePacket().setHeader(packetHeader).setMessage(message);
        Channel channel = channelFuture.channel();
        channel.write(gson.toJson(packet));

        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    // TODO: 16/8/30 统计
                } else {
                    LOGGER.warn("send error topic = {}, message = {}", topic, message);
                }
            }
        });

        // TODO: 16/8/30 异步优化
        channel.flush();
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
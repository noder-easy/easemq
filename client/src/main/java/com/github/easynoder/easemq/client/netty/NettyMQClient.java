package com.github.easynoder.easemq.client.netty;

import com.github.easynoder.easemq.client.handler.TcpClientHandler;
import com.github.easynoder.easemq.commons.HostPort;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
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

public class NettyMQClient implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyMQClient.class);

    private HostPort hostPort;

    public NettyMQClient() {
        this(new HostPort());
    }

    public NettyMQClient(HostPort hostPort) {
        this.hostPort = hostPort;
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
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

                    pipeline.addLast("handler", new TcpClientHandler());
                }
            });

            ChannelFuture f = b.connect(this.hostPort.getHost(), this.hostPort.getPort()).sync();
            LOGGER.info("Netty client connected! hostport: {}", this.hostPort);
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1; i++) {
            new Thread(new NettyMQClient(), ">>>this thread " + i).start();
        }
        while (true) ;
    }
}
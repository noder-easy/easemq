package com.github.easynoder.easemq.server.netty;

import com.github.easynoder.easemq.commons.HostPort;
import com.github.easynoder.easemq.core.Message;
import com.github.easynoder.easemq.core.exception.StoreException;
import com.github.easynoder.easemq.server.IMQServer;
import com.github.easynoder.easemq.server.NettyMQServerClientManager;
import com.github.easynoder.easemq.server.handler.TcpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.apache.catalina.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Desc:
 * Author:easynoder
 * Date:16/7/17
 * E-mail:easynoder@outlook.com
 */
public class NettyMQServer implements IMQServer{

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyMQServer.class);

    private static final int BOSS_GROUP_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int WORKER_GROUP_SIZE = 100;

    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS_GROUP_SIZE);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(WORKER_GROUP_SIZE);

    private ChannelFuture channelFuture;
    private HostPort hostPort;

    NettyMQServerClientManager clienManager = new NettyMQServerClientManager();

    public NettyMQServer() {
        this(new HostPort());
    }

    public NettyMQServer(HostPort hostPort) {
        Assert.notNull(hostPort, "server bind hostport can't be null!");
        this.hostPort = hostPort;
        new Thread(clienManager).start();
    }

    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {

                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        pipeline.addLast(new LengthFieldPrepender(4));
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new TcpServerHandler(clienManager));

                    }
                });
        channelFuture = bootstrap.bind(this.hostPort.getHost(), this.hostPort.getPort()).sync();
        LOGGER.info("netty mq-server started! bind hostport {}", this.hostPort);
    }

    public void send(String topic, Message message) {

        // TODO: 16/8/25
    }

    public void close() {
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    protected void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }


    public static void main(String[] args) throws InterruptedException {
        System.out.println("start netty mq-server....");
        new NettyMQServer().start();


    }

}

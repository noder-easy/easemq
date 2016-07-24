package com.github.easynoder.easemq.server.netty;

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

/**
 * Desc:
 * Author:easynoder
 * Date:16/7/17
 * E-mail:easynoder@outlook.com
 */
public class NettyMQServer {


    private static final int BOSS_GROUP_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int WORKER_GROUP_SIZE = 100;

    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS_GROUP_SIZE);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(WORKER_GROUP_SIZE);

    public static void start() throws InterruptedException {
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
                        pipeline.addLast(new TcpServerHandler());

                    }
                });
        ChannelFuture cf = bootstrap.bind("localhost", 2770).sync();
        cf.channel().closeFuture().sync();
        System.out.println("server started!");
    }


    protected static void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }


    public static void main(String[] args) throws InterruptedException {
        System.out.println("start tcp server....");
        NettyMQServer.start();
    }

}

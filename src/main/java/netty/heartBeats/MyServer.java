package netty.heartBeats;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Author: tobi
 * @Date: 2020/7/6 21:40
 *
 * Netty 心跳检测例子
 **/
public class MyServer {
    public static void main(String[] args) throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))  //在 bossGroup 添加一个日志处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            /**
                             * IdleStateHandler 是 Netty提供的处理空闲状态的处理器
                             *
                             * readerIdleTime：表示多长时间没有读，就发送一个心跳包检测是否连接
                             * writerIdleTime：表示多长时间没有写，就发送一个心跳包检测是否连接
                             * allIdleTime：表示多长时间没有读写，就发送一个心跳包检测是否连接
                             * 时间单位：
                             */
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS))
                                    //加入一个对空闲检测进一步处理的handler（自定义）。
                                    //当 IdleStateHandler 处理后，就会传递给 pipeline 的下一个处理器去处理（通过调用下一个 handler 的 userEventTiggered）
                                    //在 userEventTiggered 方法中 去处理IdleStateEvent （读空闲，写空闲，读写空闲）
                                    .addLast(new MyServerHandler());
                        }
                    });

            ChannelFuture cf = bootstrap.bind(8001).sync();
            cf.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

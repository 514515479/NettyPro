package netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import netty.heartBeats.MyServerHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Author: tobi
 * @Date: 2020/7/6 22:34
 *
 * websockt长连接开发
 **/
public class MyWebsocketServer {
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
                            ch.pipeline()
                                    //因为是http协议，使用http的编码和解码器
                                    .addLast("MyHttpServerCodec", new HttpServerCodec())
                                    //是以块方式写，添加 ChunkedWriteHandler 处理器
                                    .addLast(new ChunkedWriteHandler())
                                    /**
                                     * 1.因为http数据在传输过程中是分段的，HttpObjectAggregator 就是可以将多个段聚合
                                     * 2.这就是为什么当浏览器发送大量数据时，就会发出多次http请求
                                     */
                                    .addLast(new HttpObjectAggregator(8192))
                                    /**
                                     * 1.对于websocket，它的数据是以帧（frame）的形式传递
                                     * 2.可以看到 WebSocketFrame 下面有6个子类（我们用到 TextWebSocketFrame）
                                     * 3.浏览器发送请求时 ws://localhost:8001/hello 表示请求的uri
                                     * 4.WebSocketServerProtocolHandler 将一个 http协议 升级为 websocket协议，保持长连接（是通过一个状态码101）
                                     */
                                    .addLast(new WebSocketServerProtocolHandler("/hello"))
                                    //自定义的handler，处理业务逻辑
                                    .addLast(new MyTextWebSocketFrameHandler());
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

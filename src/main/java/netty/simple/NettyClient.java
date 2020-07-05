package netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Author: tobi
 * @Date: 2020/7/5 15:45
 *
 * Netty的简单客户端
 **/
public class NettyClient {
    public static void main(String[] args) {
        //客户端需要一个事件循环组
        EventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            //创建客户端启动对象，配置参数（注意服务端用的是ServerBootstrap，客户端用的是Bootstrap）
            Bootstrap bootstrap = new Bootstrap();
            //使用链式编程来设置
            bootstrap.group(eventExecutors)  //设置线程组
                    .channel(NioSocketChannel.class)  //使用 NioSocketChannel 作为客户端的通道实现
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //pipeline()：通过SocketChannel返回关联的pipeline
                            //addLast()：在pipeline最后增加一个handler（这里是自定义的handler）
                            ch.pipeline().addLast(new NettyClientHandler());
                        }
                    });
            System.out.println("========客户端 is Ready========");
            //启动客户端去连接服务端
            ChannelFuture cf = bootstrap.connect("127.0.0.1", 6668).sync();
            //对关闭通道进行监听（当有关闭通道的事件的时候，才会去处理）
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }
}

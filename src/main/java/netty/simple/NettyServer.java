package netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author: tobi
 * @Date: 2020/7/4 21:20
 *
 * Netty的简单服务端
 **/
public class NettyServer {
    public static void main(String[] args) {
        //创建 BossGroup 和 WorkGroup
        //说明
        //1.创建两个线程组 bossGroup 和 workerGroup
        //2.bossGroup 只处理连接请求，真正的客户端业务处理，交给 workerGroup
        //3.bossGroup 和 workerGroup都是无限循环
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //创建服务端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //使用链式编程来设置
            bootstrap.group(bossGroup, workerGroup)  //设置两个线程组
                    .channel(NioServerSocketChannel.class)  //使用 NioServerSocketChannel 作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG, 128)  //设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  //设置保持连接活动状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {  //创建一个通道测试对象（匿名方式）
                        //给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //pipeline()：通过SocketChannel返回关联的pipeline
                            //addLast()：在pipeline最后增加一个handler（这里是自定义的handler）
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    }); //给 workerGroup 的 EventLoop 对应的管道设置处理器
            System.out.println("========服务器 is Ready========");
            //启动服务器（绑定一个端口并且同步，生成一个ChannelFuture对象（立马返回））
            ChannelFuture cf = bootstrap.bind(6668).sync();
            //对关闭通道进行监听（当有关闭通道的事件的时候，才会去处理）
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅的关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

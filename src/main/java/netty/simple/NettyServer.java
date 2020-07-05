package netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author: tobi
 * @Date: 2020/7/4 21:20
 *
 * Netty的简单服务端
 *
 * Netty是主从Reactor模型
 **/
public class NettyServer {
    public static void main(String[] args) {
        //创建 BossGroup 和 WorkGroup
        //说明
        //1.创建两个线程组 bossGroup 和 workerGroup
        //2.bossGroup 只处理连接请求，真正的客户端业务处理，交给 workerGroup
        //3.bossGroup 和 workerGroup 都是无限循环
        //4.bossGroup 和 workerGroup 含有的子线程（NioEventLoop）个数默认是CPU核数 * 2
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
                            //这里可以用一个集合管理 SocketChannel，在推送消息时，将业务加入到各个channel对应的NioEventLoop的taskQueue中
                            //set.add(ch);
                            //pipeline()：通过SocketChannel返回关联的pipeline
                            //addLast()：在pipeline最后增加一个handler（这里是自定义的handler）
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    }); //给 workerGroup 的 EventLoop 对应的管道设置处理器
            System.out.println("========服务器 is Ready========");
            //启动服务器（绑定一个端口并且同步，生成一个ChannelFuture对象（立马返回））
            ChannelFuture cf = bootstrap.bind(6668).sync(); // sync，不用监听也能拿到结果（等执行完毕返回给ChannelFuture，主线程才继续，同步的）

            //给cf注册监听器，监控我们关心的事件（监听返回给ChannelFuture是异步的，主线程不用等待）
            cf.addListener(future -> {
                if (cf.isSuccess()) {
                    System.out.println("监听端口6668成功");
                } else {
                    System.out.println("监听端口6668失败");
                }
            });

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

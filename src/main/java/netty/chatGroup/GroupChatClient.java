package netty.chatGroup;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import sun.applet.Main;

import java.util.Scanner;

/**
 * @Author: tobi
 * @Date: 2020/7/6 20:15
 *
 * 群聊-客户端
 **/
public class GroupChatClient {
    private final String host;
    private final int port;

    public GroupChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        EventLoopGroup wokerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(wokerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("decoder", new StringDecoder())  //解码器
                                .addLast("encoder", new StringEncoder())  //编码器
                                .addLast(new GroupChatClientHandler());  //自己定义的handler
                    }
                });

        try {
            ChannelFuture cf = bootstrap.connect(host, port).sync();
            Channel channel = cf.channel();
            System.out.println("------------" + channel.localAddress() + "------------");
            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()) {
                String msg = sc.nextLine();
                //通过channel发送给服务端
                channel.writeAndFlush(msg + "\n");
            }

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            wokerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new GroupChatClient("127.0.0.1", 8001).run();
    }
}

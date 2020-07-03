package nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author: tobi
 * @Date: 2020/7/3 19:40
 *
 * NIO入门案例，实现服务端与客户端之间的数据简单通讯（非阻塞）
 *
 * 客户端
 **/
public class NIOClient {
    public static void main(String[] args) throws Exception{
        //得到一个SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //设置服务器端的ip和port
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
        //连接服务器
        if (!socketChannel.connect(inetSocketAddress)) { //连接不成功（不成功也不会阻塞在这里）
            while (!socketChannel.finishConnect()) { //没有完成连接
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其他工作...");
            }
        }
        //连接成功，就发送数据
        String str = "HelloNio!";
        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes()); //wrap方法不用去指定Buffer的大小
        //发送数据，将buffer的数据写入socketChannel
        socketChannel.write(byteBuffer);
        System.in.read();
    }
}

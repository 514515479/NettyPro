package nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: tobi
 * @Date: 2020/7/2 16:09
 *
 * NIO（Non-blocking I/O  同步非阻塞IO jdk自带）
 *
 * 三大核心组件：
 *     1。channel（通道，可以理解成BIO的Socket）
 *     2.Buffer（缓冲区）
 *     3.Select（选择器）
 *
 * BIO和NIO的区别
 *     1.BIO以流的方式处理数据，NIO以块的方式处理数据，块IO效率比流IO高很多。
 *     2.BIO是阻塞的，NIO是非阻塞的。
 *     3.BIO是单向的，NIO是双向的。
 *     4.BIO基于字节流和字节符进行操作，NIO基于Channel和Buffer进行操作，
 *       数据总是从Channel读取到Buffer，或者从Buffer写入Channel，Selector用于监听多个Channel的事件（比如：连接请求，数据到达）
 *       因此单个线程就可以监听多个客户端Channel。
 *
 * Selector（选择器，多路复用器）
 *     1.能够检测到多个注册的Channel上是否有事件发生（注意：多个Channel以事件的方式可以注册到同一个Selector）。
 *     2.只有在 连接/通道 真正有读写事件发生时，才会进行读写，大大减小开销，并且不必为每个连接都创建一个线程，不用去维护多个线程。
 *     3.避免了多线程之间上下文切换导致的开销。
 *
 * NIO非阻塞网络编程原理
 *     1.当客户端连接时，会通过ServerSocketChannel获得SocketChannel。
 *     2.Selector进行监听select方法，返回有事件发生的SocketChannel个数。
 *     3.将SocketChannel注册到Selector上，注册后会返回一个SelectionKey，会和该Selector关联（集合）。
 *     4.进一步得到SelectionKey（有事件发生），然后通过SelectionKey反向获取到SocketChannel。
 *     5.通过的得到的SocketChannel，完成业务处理。
 *
 *
 *  NIO入门案例，实现服务端与客户端之间的数据简单通讯（非阻塞）
 *
 *  服务器端
 **/
public class NIOServer {
    public static void main(String[] args) throws Exception{
        //创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //得到Selector
        Selector selector = Selector.open();
        //绑定端口，在服务器监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //把serverSocketChannel注册到selector，关心事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //循环等待客户端连接
        while (true) {
            //这里我们等待1秒，如果没有事件就返回
            if (selector.select(1000) == 0) { //没有事件发生
                System.out.println("服务器等待了1秒，无连接...");
                continue;
            }
            //如果返回 > 0，表示已经获取到关注的事件
            //selector.selectedKeys()获取关注事件的selectionKey集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                //获取selectionKey
                SelectionKey key = keyIterator.next();
                //根据key对应的通道发生的事件做相应的处理
                if (key.isAcceptable()) { //如果发生的是OP_ACCEPT
                    //给该客户端生产一个SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept(); //这里accept不会阻塞，因为连接已经发生了（）if (key.isAcceptable())
                    System.out.println("客户端连接成功生成一个SocketChannel：" + socketChannel.hashCode());
                    //将socketChannel设置为非阻塞
                    socketChannel.configureBlocking(false);
                    //将socketChannel注册到selector，关注事件为OP_READ，并给socketChannel关联一个buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if (key.isReadable()) { //如果发生的是OP_READ
                    //通过key反向获取到socketChannel
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    //获取到socketChannel关联的buffer，从socketChannel读取数据写入buffer
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    socketChannel.read(byteBuffer);
                    System.out.println("From客户端的数据：" + new String(byteBuffer.array()));
                }
                //手动从集合中移除selectionKey，防止重复操作
                keyIterator.remove();
            }
        }
    }
}

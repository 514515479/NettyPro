package nio;

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
 **/
public class NIOServer {

}

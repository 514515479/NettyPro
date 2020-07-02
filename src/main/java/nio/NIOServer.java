package nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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
 **/
public class NIOServer {
    public static void main(String[] args) throws Exception {
        nativeWrite();
    }

    //本地文件写数据，将“Hello NIO"写入到 file01.txt中
    public static void nativeWrite() throws Exception {
        String str = "Hello NIO";

        //1.创建一个输出流
        FileOutputStream fileOutputStream = new FileOutputStream("E:\\file01.txt");
        //2.通过fileOutputStream获取对应的FileChannel
        FileChannel fileChannel = fileOutputStream.getChannel(); // 这个fileChannel的类型实际是FileChannelImpl

        //3.创建一个缓冲区byteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //4.将str放入byteBuffer，然后从byteBuffer写入到fileChannel
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();
        fileChannel.write(byteBuffer);

        //5.关闭流
        fileOutputStream.close();
    }
}

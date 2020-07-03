package nio;

import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @Author: tobi
 * @Date: 2020/7/2 16:28
 *
 * Buffer的使用
 *
 * Buffer本质上是一个可以读写的内存块，底层有一个数组（hb数组，真正的数据存放在这个数组里）。
 * （可以理解成一个含数组的容器对象，提供了一些方法，可以更轻松地使用内存卡）
 * Buffer可以读也可以写，需要flip方法切换。
 *
 * 注意：get()和put()会让position自动+1
 *
 * flip()和clear()的区别
 *     flip： limit置为position，position置为0，mark置为-1。
 *     clear：limit置为capacity，position置为0，mark置为-1。
 **/
public class BasicBuffer {
    public static void main(String[] args) throws Exception{
        //readAndGet();
        //type();
        //readOnly();
        //mappedByteBuffer();
        scatteringAndGathering();
    }

    //Buffer基本的写数据和取数据
    public static void readAndGet() {
        //创建一个Buffer，大小为5，可以存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);

        //向Buffer存放数据
//        intBuffer.put(10);
//        intBuffer.put(11);
//        intBuffer.put(12);
//        intBuffer.put(13);
//        intBuffer.put(14);
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }

        //如何取出Buffer数据
        //讲Buffer翻转，读写切换。
        //flip会修改BUffer的4个状态：
        //   1。mark：标记。
        //   2.position：下个被读或被写的元素的索引，每次读写都会改变该值。flip该值重置为0
        //   3.limit：读/写的终点，不能对超过limit的位置读写，该值可修改（最大可以读取多少个）position不能超过limit。
        //   4.capacity：容纳的最大数据量，Buffer创建的时候设定，不能被修改。
        intBuffer.flip();

        //如果Buffer里面还有数据
        while (intBuffer.hasRemaining()) {
            //get里面维护了一个索引，每get一次，索引就往后移动一次
            System.out.println(intBuffer.get());  //输出0，2，4，6，8
        }

        System.out.println("---");

        //从索引1开始读数据
        intBuffer.position(1);
        //读到索引3的位置就不读了
        intBuffer.limit(3);
        //清除缓冲区，各个状态都恢复到初始状态，但是数据还在，加了这句这里会输出0，2，4，6，8
        //intBuffer.clear();
        //intBuffer.put(1);  //clear后再写数据，会从第一个覆盖，然后position会自动加1
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());  //输出2，4
        }
        System.out.println("---");
    }

    //Buffer取数据要按对应的类型取
    private static void type() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);

        byteBuffer.putInt(1);
        byteBuffer.putLong(2);
        byteBuffer.putChar('零');
        byteBuffer.putShort((short) 5);

        byteBuffer.flip();
        //不按对应的类型取会报错（顺序）
        System.out.println(byteBuffer.getInt());
        System.out.println(byteBuffer.getLong());
        System.out.println(byteBuffer.getChar());
        System.out.println(byteBuffer.getShort());
        System.out.println("---");
    }

    //获取只读的Buffer
    private static void readOnly() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);
        for (int i = 0; i < byteBuffer.capacity(); i++) {
            byteBuffer.put((byte) i);
        }

        byteBuffer.flip();

        //得到只读的Buffer，往里面写数据会抛ReadOnlyBufferException异常
        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
        while (readOnlyBuffer.hasRemaining()) {
            System.out.println(readOnlyBuffer.get());
        }
    }

    //MappedByteBuffer 可以让文件直接在内存（堆外的内存）中进行修改，操作系统不需要拷贝一份，性能较高，而如何同步到文件由NIO完成。
    //这里做的是直接在内存修改文件file03.txt中的内容
    private static void mappedByteBuffer() throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile("E:\\file03.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        /**
         * 参数1：模式，这里的FileChannel.MapMode.READ_WRITE使用的是读写模式。
         * 参数2：可以直接修改的起始位置。
         * 参数3：映射到内存的大小（不是索引），单位是字节（这里最多可以映射file03.txt的5个字节到内存去修改，可以修改的范围就是0-5）
         */
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        mappedByteBuffer.put(0, (byte)'A');
        mappedByteBuffer.put(3, (byte)'9');

        randomAccessFile.close();
        System.out.println("修改成功...");
    }

    //Buffer的分散scattering和聚集gathering
    //scattering：将数据写入到Buffer时，可以采用Buffer数组，依次写入
    //gathering：从Buffer读取数据时，可以采用Buffer数组，依次读
    public static void scatteringAndGathering() throws Exception{
        //使用ServerSocketChannel和SocketChannel网络
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);
        //绑定端口到socket，并启动
        serverSocketChannel.socket().bind(inetSocketAddress);

        //创建Buffer数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        //等待客户端连接（telnet）
        SocketChannel socketChannel = serverSocketChannel.accept();
        //循环读取
        int messageLength = 8; //假定从客户端读取8个字节
        while (true) {
            int byteRead = 0;
            while (byteRead < messageLength) {
                long l = socketChannel.read(byteBuffers);
                byteRead += l; //累计读取的字节
                System.out.println("byteRead = " + byteRead);
                //使用流打印当前Buffer的position和limit
                Arrays.asList(byteBuffers).stream()
                        .map(buffer -> "position = " + buffer.position() + "; limit = " + buffer.limit())
                        .forEach(System.out::println);
            }

            //将所有的Buffer进行flip
            Arrays.asList(byteBuffers).forEach(buffer -> buffer.flip());
            //将数据读出，显示到客户端（这里是为了展示依次读）
            long byteWrite = 0;
            while (byteWrite < messageLength) {
                long l = socketChannel.write(byteBuffers);
                byteWrite += l;
            }
            //将所有Buffer进行clear
            Arrays.asList(byteBuffers).forEach(buffer -> buffer.clear());
            System.out.println("byteRead:" + byteRead + "; byteWrite:" + byteWrite + "; messageLength:" + messageLength);
        }
    }
}

package nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author: tobi
 * @Date: 2020/7/2 20:20
 *
 * Channel的使用
 *
 * 可是实现异步读写数据，可以从缓冲区读数据，也可以写数据到缓冲区
 *
 * ServerSocketChannel类似BIO的ServerSocket；SocketChannel类似BIO的Socket。
 **/
public class BasicChannel {
    public static void main(String[] args) throws Exception {
        nativeWrite();
        //nativeRead();
    }

    //本地文件写数据，将“Hello NIO"写入到 file01.txt中
    public static void nativeWrite() throws Exception {
        String str = "Hello NIO你好";

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

    //本地文件读数据，将file01.txt中的内容读到str
    public static void nativeRead() throws Exception {
        //1.创建文件的输入流
        File file = new File("E:\\file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        //2.通过fileInputStream获取对应的FileChannel
        FileChannel fileChannel = fileInputStream.getChannel(); // 这个fileChannel的类型实际是FileChannelImpl

        //3.创建一个缓冲区byteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
        //4.将fileChannel的数据写入byteBuffer
        fileChannel.read(byteBuffer);

        //打印
        //System.out.println(new String(byteBuffer.array()));

        //5.关闭流
        fileInputStream.close();

    }
}

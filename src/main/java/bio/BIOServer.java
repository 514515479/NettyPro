package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: tobi
 * @Date: 2020/7/2 0:39
 *
 * BIO例子（jdk自带的IO）
 **/
public class BIOServer {
    public static void main(String[] args) throws IOException {
        //线程池机制
        //1.创建一个线程池
        //2.如果有客户端连接，就创建一个线程与之通讯（单独写一个方法）
        ExecutorService pool = Executors.newCachedThreadPool();

        //创建serverSocket
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动了...");
        while (true) {
            //监听，等待客户端连接
            final Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端");

            //创建一个线程与之通讯（单独写一个方法）
            pool.execute(() -> {
                //可以和客户端通讯
                handler(socket);
            });
        }
    }

    //和客户端通讯（客户端连接方式：cmd窗口，telnet 127.0.0.1 6666  ctrl+]  send hello发送数据）
    public static void handler(Socket socket) {
        try {
            byte[] bytes = new byte[1000];
            InputStream inputStream = socket.getInputStream();
            //循环读取客户端发送的数据
            while (true) {
                int read = inputStream.read(bytes);
                if (read != 1) {
                    //输出客户端发送的数据
                    System.out.println("当前线程名：" + Thread.currentThread().getName() + " 内容：" + new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("关闭和客户端的连接");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //try-with-recourse（优雅的关闭资源，不用在try-catch-finally里面套try-catach）
        /*try (InputStream inputStream = socket.getInputStream()) {
            byte[] bytes = new byte[1000];
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}

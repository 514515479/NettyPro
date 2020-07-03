package nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Author: tobi
 * @Date: 2020/7/3 21:41
 *
 * NIO群聊系统 客户端
 *
 * 1.连接服务器
 * 2.发送消息
 * 3.接受服务器端的消息
 *
 * 注意事项：
 *     SocketChannel必须设置通道为非阻塞，才能向Selector注册
 **/
public class GroupChatClient {
    //服务器ip
    private static final String HOST = "127.0.0.1";
    //服务器port
    private static final int PORT = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    //构造器，初始化工作
    public GroupChatClient() throws Exception{
        //得到selector
        selector = Selector.open();
        //连接服务器
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //selector
        socketChannel.register(selector, SelectionKey.OP_READ);
        //得到userName
        username = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(username + " 上线了...");
    }

    //向服务器发送消息
    public void sendInfo(String info) {
        info = username + "说：" + info;
        try {
            //将info写入socketChannel
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取从服务器端回复（转发）的消息
    public void readInfo() {
        try {
            int count = selector.select();
            if (count > 0) { //如果count > 0，说明有事件要处理
                Iterator<SelectionKey> keyIterator = selector.keys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) { // 如果有读事件
                        //根据selectionKey获取对应的socketChannel
                        SocketChannel sc = (SocketChannel) key.channel();
                        //得到Buffer，并把sc通道的数据写入buffer
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        sc.read(byteBuffer);
                        String msg = new String(byteBuffer.array());
                        System.out.println(msg.trim());
                    }
                    //删除当前selectionKey，防止重复操作
                    keyIterator.remove();
                }
            } else {
                System.out.println("没有可用的通道...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        //启动我们客户端
        GroupChatClient chatClient = new GroupChatClient();
        //启动一个线程，每隔3秒，接受服务器发送数据（别的client发的消息）
        new Thread(() -> {
            while (true) {
                chatClient.readInfo();
                try {
                    Thread.currentThread().sleep(3000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //发送数据给服务器端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }
    }
}

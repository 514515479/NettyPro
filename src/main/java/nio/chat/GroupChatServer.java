package nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @Author: tobi
 * @Date: 2020/7/3 21:40
 *
 * NIO群聊系统 服务端
 *
 * 1.服务器启动，并监听端口6667
 * 2.服务器接收客户端的消息，并实现转发[实现上线和离线]
 **/
public class GroupChatServer {
    private Selector selector;
    private ServerSocketChannel listenChannel; //专门做监听的
    private static final int PORT = 6667;

    //构造器做一些初始化工作
    public GroupChatServer() {
        try {
            //得到选择器
            selector = Selector.open();
            //得到ServerSocketChannel
            listenChannel = ServerSocketChannel.open();
            //绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            //设置非阻塞
            listenChannel.configureBlocking(false);
            //注册到selector上
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //监听
    public void listen() {
        try {
            //循环处理
            while (true) {
                int count = selector.select(2000);
                if (count > 0) {  //如果count > 0，说明有事件要处理
                    //遍历得到的selectionKeys
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        //取出selectionKey
                        SelectionKey key = keyIterator.next();
                        //对不同的监听事件做处理
                        //处理连接事件
                        if (key.isAcceptable()) {
                            SocketChannel sc = listenChannel.accept();
                            sc.configureBlocking(false);
                            //将socketChannel注册到selector
                            sc.register(selector, SelectionKey.OP_READ);
                            //连接成功了，提示“XXX上线了”
                            System.out.println(sc.getRemoteAddress() + "上线了...");

                        }
                        //处理读事件，即通道是可读状态
                        if (key.isReadable()) {
                            readData(key);
                        }
                        //当前的key删除，防止重复处理
                        keyIterator.remove();
                    }

                } else {
                    //System.out.println("服务器等待中...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    //读取客户端消息
    private void readData(SelectionKey key) throws Exception{
        SocketChannel socketChannel = null;
        try {
            //取到关联的SocketChannel
            socketChannel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int read = socketChannel.read(byteBuffer);
            if (read > 0) { //如果读取到了，就把byteBuffer的数据转换成字符串
                String msg = new String(byteBuffer.array());
                //输出该消息
                System.out.println("From客户端" + msg);
                //群聊系统，需要向其它客户端（排除自己）转发消息
                this.sendInfoToOtherClient(msg, socketChannel);
            }

        } catch (Exception e) {
            //读不到数据会抛出异常，可能是因为socketChannel已经关闭了（下线了）
            System.out.println(socketChannel.getRemoteAddress() + "离线了...");
            //取消注册，关闭socketChannel
            key.cancel();
            socketChannel.close();
        }
    }

    //转发消息给其它客户端
    private void sendInfoToOtherClient(String msg, SocketChannel self) throws Exception{
        System.out.println("服务器消息转发中...");
        //遍历所有注册到Selector上的没啥难度的问题，并排除自己
        for (SelectionKey key : selector.keys()) {
            //通过SelectionKey获取对应的SocketChannel
            Channel targetChannel = key.channel();
            //排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != self) {
                //转型
                SocketChannel dest = (SocketChannel) targetChannel;
                //将msg存到buffer，然后写入dest
                dest.write(ByteBuffer.wrap(msg.getBytes()));
            }
        }
    }

    public static void main(String[] args) throws Exception{
        //创建服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}

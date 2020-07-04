package netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @Author: tobi
 * @Date: 2020/7/5 4:38
 *
 * Netty的简单服务端的自定义Handler处理器
 *
 * 自定义一个Handler，需要继承netty规定好的某个HandlerAdapter（规范）。
 **/
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 读取数据事件（这里我们可以读取客户端发送的消息）
     * @param ctx 上下文对象，含有管道pipeline，通道channel，连接地址
     * @param msg 客户端发送的数据 默认Object
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ChannelHandlerContext ctx：" + ctx);
        //将msg转成一个ByteBuf（netty提供的（性能更高），不是NIO的ByteBuffer）
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送的数据是：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //write方法 + flush方法
        //将数据写到缓存，并刷新（不刷新就不会写到通道）
        //一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello client客户端", CharsetUtil.UTF_8));
    }

    //处理异常，一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

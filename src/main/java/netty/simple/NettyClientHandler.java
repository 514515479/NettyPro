package netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @Author: tobi
 * @Date: 2020/7/5 16:27
 *
 * Netty的简单客户端的自定义Handler处理器
 *
 * 自定义一个Handler，需要继承netty规定好的某个HandlerAdapter（规范）。
 **/
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    //当通道就绪就会触发该方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelHandlerContext ctx：" + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello Server服务器", CharsetUtil.UTF_8));
    }

    /**
     * 读取数据事件（当通道有读取事件时会触发，这里我们可以读取客户端发送的消息）
     * @param ctx 上下文对象，含有管道pipeline，通道channel，连接地址
     * @param msg 客户端发送的数据 默认Object
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ChannelHandlerContext ctx：" + ctx);
        //将msg转成一个ByteBuf（netty提供的（性能更高），不是NIO的ByteBuffer）
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("服务器回复的消息是：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器地址：" + ctx.channel().remoteAddress());
    }

    //处理异常，一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

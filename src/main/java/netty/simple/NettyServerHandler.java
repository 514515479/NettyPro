package netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @Author: tobi
 * @Date: 2020/7/5 4:38
 *
 * Netty的简单服务端的自定义Handler处理器
 *
 * ChannelHandlerContext（ctx）包含了 channel 和 pipeline，channel 和 pipeline 相互包含。
 *
 * 自定义一个Handler，需要继承netty规定好的某个HandlerAdapter（规范）。
 **/
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 读取数据事件（当通道有读取事件时会触发，这里我们可以读取客户端发送的消息）
     * @param ctx 上下文对象，含有管道pipeline，通道channel，连接地址
     * @param msg 客户端发送的数据 默认Object
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        /**
         * 问题描述：比如这里我们有一个非常耗时长的业务
         * 解决方案：异步执行 -> 提交该 channel 对应的 NioEventLoop 的 taskQueue中
         *
         * Thread.sleep(10 * 1000);  //模拟耗时长操作
         * ctx.writeAndFlush(Unpooled.copiedBuffer("From channelRead：耗时长操作！", CharsetUtil.UTF_8));
         * System.out.println("go on...");
         */

        //解决方案1
        //  用户程序自定义普通任务（会被提交到和 channel 关联的 NioEventLoop 的 taskQueue中）
        //  可以加多个任务（NioEventLoop 会按照顺序先后执行这些任务， 注意任务队列中的任务 是先后执行， 不是同时执行）
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(10 * 1000);  //模拟耗时长操作
                ctx.writeAndFlush(Unpooled.copiedBuffer("From channelRead：耗时长操作！", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("go on...");
        });

        //解决方案2
        //  用户自定义定时任务，该任务提交到 ScheduleTaskQueue中
        ctx.channel().eventLoop().schedule(() -> {
            try {
                Thread.sleep(10 * 1000);  //模拟耗时长操作
                ctx.writeAndFlush(Unpooled.copiedBuffer("From channelRead：耗时长操作！", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("go on...");
        }, 5, TimeUnit.SECONDS);

        //解决方案3
        //  非Reactor线程调用Channel的各种方法



        //-----------------------------------------------------------------------------------------------------------

        /*System.out.println("ChannelHandlerContext ctx：" + ctx);
        System.out.println("看看Channel和pipeline的关系：相互包含");
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline();  //本质是一个双向链表，出栈入栈问题

        //将msg转成一个ByteBuf（netty提供的（性能更高），不是NIO的ByteBuffer）
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送的消息是：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());*/
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

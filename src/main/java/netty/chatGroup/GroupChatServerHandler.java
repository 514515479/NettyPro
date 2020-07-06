package netty.chatGroup;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

/**
 * @Author: tobi
 * @Date: 2020/7/6 19:14
 *
 * 群聊-服务器自定义handler
 **/
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {
    //定义一个channel组，管理所有的channel
    //GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //handlerAdded 表示连接建立，一旦连接，第一个被执行
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户端加入的消息推送给其他客户端
        //channelGroup 的 writeAndFlush 该方法会将 channelGroup 中的所有 channel 遍历，并发送消息，不需要我们自己遍历
        channelGroup.writeAndFlush("[客户端] " + channel.remoteAddress() + " 加入聊天");
        //将当前的 channel 加入 channelGroup
        channelGroup.add(channel);
    }

    //表示channel处于活跃状态，提示 XXX 上线了
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了！");
    }

    //表示channel处于非活跃状态，提示 XXX 离开了
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "离线了！");
    }

    //断开连接，将 XXX 离开了 推送给其他在线客户端
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.writeAndFlush("[客户端] " + ctx.channel().remoteAddress() + " 离开聊天");
        //这里 handlerRemoved 执行会自己把 channelGroup 中的 channel 移除掉，不需要手动处理
        System.out.println("当前channelGroup大小：" + channelGroup.size());
    }

    //读取数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.stream()
                .forEach(ch -> {
                    if (ch != channel) {  //给别人发送消息
                        ch.writeAndFlush("[客户端] " + ch.remoteAddress() + " 发送了消息：" + msg + "\n");
                    } else {  //回显自己发送的消息
                        ch.writeAndFlush("[自己] " + ch.remoteAddress() + " 发送了消息：" + msg + "\n");
                    }
                });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //发生异常关闭通道
        ctx.close();
    }
}

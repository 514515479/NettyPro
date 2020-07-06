package netty.chatGroup;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;

/**
 * @Author: tobi
 * @Date: 2020/7/6 20:22
 *
 * 群聊-客户端自定义handler
 **/
public class GroupChatClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg.trim());
    }
}

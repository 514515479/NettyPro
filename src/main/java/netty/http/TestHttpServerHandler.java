package netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @Author: tobi
 * @Date: 2020/7/5 23:52
 *
 * 自定义handler
 *
 * SimpleChannelInboundHandler 是 ChannelInboundHandler 的子类，提供的方法更多些
 * HttpObject 客户端和服务器相互通讯的数据被封装成 HttpObject
 **/
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    //读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        //判断 msg 是不是一个HttpRequest请求
        if (msg instanceof HttpRequest) {
            System.out.println("msg 类型 = " + msg.getClass());
            System.out.println("客户端地址：" + ctx.channel().remoteAddress());

            //每个浏览器对应的 pipeline 和 handler 是独立的，不会相互共享
            //http用完就断，再次刷新页面就是新的 pipeline 和 handler
            //问题：为什么会请求两次？（有一次是图标favicon.ico）
            //获取到httpRequest
            HttpRequest httpRequest = (HttpRequest) msg;
            //获取到uri
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                System.out.println("请求了favicon.ico，不做处理");
            }


            //回复信息给浏览器[http协议]
            ByteBuf byteBuf = Unpooled.copiedBuffer("Hello 我是服务器！", CharsetUtil.UTF_8);

            //构造一个http响应，即HttpResponse
            //参数1：http版本
            //参数2：响应码
            //参数3：内容
            FullHttpResponse respone = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);

            //设置返回头
            respone.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
            respone.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

            //将构建好的 respone 返回
            ctx.writeAndFlush(respone);
        }
    }
}

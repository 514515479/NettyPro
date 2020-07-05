package netty.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @Author: tobi
 * @Date: 2020/7/5 23:43
 *
 * 自定义初始化器
 **/
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //得到pipeline
        ChannelPipeline pipeline = ch.pipeline();
        //1.加入一个netty提供的 处理http编解码器 httpServeCodec
        pipeline.addLast("MyHttpServerCodec", new HttpServerCodec()); // 自定义名称
        //2.加入一个自定义的handler
        pipeline.addLast("MyHttpServerHandler", new TestHttpServerHandler());
    }
}

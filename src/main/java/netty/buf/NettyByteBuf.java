package netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * @Author: tobi
 * @Date: 2020/7/6 16:33
 *
 * netty 的 ByteBuf 例子
 *
 *  1.Netty 的 Bytebuf 包含一个数组arr。
 *  2.Netty 的 ByteBuf 不像NIO需要 flip读写转换。
 *  3.底层维护了 readerIndex 和 writeIndex，通过 readerIndex，writeIndex，capacity 将 ByteBuf分成3个区域
 *    1. 0 - readerIndex 已经读取范围
 *    2. readerIndex - writeIndex 可读的区域
 *    3. writeIndex - capacity  可写的区域
 **/
public class NettyByteBuf {
    public static void main(String[] args) {
        //创建方式一，该对象包含一个数组arr，是一个byte[10]
        ByteBuf buf1 = Unpooled.buffer(10);
        for (int i = 0; i < 10; i++) {
            buf1.writeByte(i);
        }

        //输出方式一
        for (int i = 0; i < buf1.capacity(); i++) {
            System.out.println(buf1.getByte(i));
        }

        //输出方式二
        for (int i = 0; i < buf1.capacity(); i++) {
            System.out.println(buf1.readByte());
        }

        //创建方式二
        ByteBuf buf2 = Unpooled.copiedBuffer("Hello ByteBuf", CharsetUtil.UTF_8);
        if (buf2.hasArray()) {
            byte[] content = buf2.array();
            //转成字符串输出
            System.out.println(new String(content, CharsetUtil.UTF_8));
            System.out.println("byteBuf = " + buf2);
        }
    }
}

package nio;

import java.nio.IntBuffer;

/**
 * @Author: tobi
 * @Date: 2020/7/2 16:28
 *
 * Buffer的使用
 *
 * Buffer本质上是一个可以读写的内存块，底层有一个数组（hb数组，真正的数据存放在这个数组里）。
 * （可以理解成一个含数组的容器对象，提供了一些方法，可以更轻松地使用内存卡）
 * Buffer可以读也可以写，需要flip方法切换。
 **/
public class BasicBuffer {
    public static void main(String[] args) {
        //创建一个Buffer，大小为5，可以存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);

        //向Buffer存放数据
//        intBuffer.put(10);
//        intBuffer.put(11);
//        intBuffer.put(12);
//        intBuffer.put(13);
//        intBuffer.put(14);
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }

        //如何取出Buffer数据
        //讲Buffer翻转，读写切换。
        //flip会修改BUffer的4个状态：
        //   1。mark：标记。
        //   2.position：下个被读或被写的元素的索引，每次读写都会改变该值。
        //   3.limit：Buffer当前的终点，不能对Buffer超过极限的位置读写，该值可修改（最大可以读取多少个）
        //   4.capacity：容纳的最大数据量，Buffer创建的时候设定，不能被修改。
        intBuffer.flip();
        //如果Buffer里面还有数据
        while (intBuffer.hasRemaining()) {
            //get里面维护了一个索引，每get一次，索引就往后移动一次
            System.out.println(intBuffer.get());
        }
    }
}

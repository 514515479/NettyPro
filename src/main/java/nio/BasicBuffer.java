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
 *
 * 注意：get()和put()会让position自动+1
 *
 * flip()和clear()的区别
 *     flip： limit置为position，position置为0，mark置为-1。
 *     clear：limit置为capacity，position置为0，mark置为-1。
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
        //   2.position：下个被读或被写的元素的索引，每次读写都会改变该值。flip该值重置为0
        //   3.limit：读/写的终点，不能对超过limit的位置读写，该值可修改（最大可以读取多少个）position不能超过limit。
        //   4.capacity：容纳的最大数据量，Buffer创建的时候设定，不能被修改。
        intBuffer.flip();

        //如果Buffer里面还有数据
//        while (intBuffer.hasRemaining()) {
//            //get里面维护了一个索引，每get一次，索引就往后移动一次
//            System.out.println(intBuffer.get());  //输出0，2，4，6，8
//        }

        System.out.println("---");

        //从索引1开始读数据
        intBuffer.position(1);
        //读到索引3的位置就不读了
        intBuffer.limit(3);
        //清除缓冲区，各个状态都恢复到初始状态，但是数据还在，加了这句这里会输出0，2，4，6，8
        //intBuffer.clear();
        //intBuffer.put(1);  //clear后再写数据，会从第一个覆盖，然后position会自动加1
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());  //输出2，4
        }
    }
}

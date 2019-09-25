package com.hzm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示MESA管程模型解决并发问题
 * <p>
 * sychronize 配合 wait 跟 notify 属于java语言内置的管程
 * <p>
 * 通过并发包的lock 跟 condition 可以实现多条件等管程模型
 */
public class MonitorDemo {

    private static List<Integer> LIST = new ArrayList<>(10);

    private static ReentrantLock lock = new ReentrantLock();

    private final Condition notFullCon;

    private final Condition notEmptyCon;

    MonitorDemo() {
        this.notEmptyCon = lock.newCondition();
        this.notFullCon = lock.newCondition();
    }

    public void push(Integer i) {
        lock.lock();
        while (LIST.size() == 10) {
            try {
                notFullCon.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LIST.add(i);
        notEmptyCon.signal();
        lock.unlock();
    }

    public Integer pop() {
        lock.lock();
        while (LIST.isEmpty()) {
            try {
                boolean await = notEmptyCon.await(10, TimeUnit.SECONDS);
                //如果等待超过10秒线程还是没有被signal唤醒,自动唤醒
                if (!await) {
                    System.out.println("等待超时...");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Integer remove = LIST.remove(LIST.size() - 1);
        notFullCon.signal();
        lock.unlock();
        return remove;
    }

}


class Test {
    public static void main(String[] args) {
        MonitorDemo monitorDemo = new MonitorDemo();
        monitorDemo.push(1);
        Integer pop = monitorDemo.pop();
        System.out.println(pop);
        Integer pop1 = monitorDemo.pop();
        System.out.println(pop1);
    }
}

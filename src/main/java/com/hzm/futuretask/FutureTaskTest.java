package com.hzm.futuretask;

import java.util.concurrent.*;

/**
 * 演示futureTask 的创建及使用,可以很方便地用在线程间同步,做参数传递
 */
public class FutureTaskTest {

    public static void main(String[] args) {
        FutureTask<String> futureTask1 = new FutureTask<>(new Task1());
        FutureTask<String> futureTask2 = new FutureTask<>(new Task2(futureTask1));
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2,
                2,
                100,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(2),
                new ThreadPoolExecutor.AbortPolicy());
        pool.submit(futureTask1);
        pool.submit(futureTask2);
    }
}

class Task1 implements Callable<String> {

    @Override
    public String call() throws Exception {
        System.out.println("拿茶叶...");
        System.out.println("洗茶叶...");
        Thread.sleep(1000);
        return "普洱茶";
    }
}

class Task2 implements Callable<String> {

    private FutureTask<String> task1;

    public Task2(FutureTask<String> task) {
        this.task1 = task;
    }


    @Override
    public String call() throws Exception {
        System.out.println("洗茶壶...");
        System.out.println("洗茶杯...");
        //等待拿茶叶的准备工作做好了才能开始泡茶
        String s = task1.get();
        System.out.println("拿到茶叶" + s);
        System.out.println("泡茶...");
        System.out.println("上茶...");
        return "success";
    }
}
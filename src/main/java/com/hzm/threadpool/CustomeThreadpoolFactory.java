package com.hzm.threadpool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomeThreadpoolFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        return new Thread("自定义名称");
    }

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 100, 120, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
                new CustomeThreadpoolFactory(), new ThreadPoolExecutor.AbortPolicy());

        threadPoolExecutor.execute(() -> System.out.println(Thread.currentThread().getName()));
    }
}

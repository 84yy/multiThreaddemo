package com.hzm.threadpool;

import java.util.concurrent.*;

public class CustomeThreadpoolFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        return new Thread("自定义名称");
    }

    public static void main(String[] args) {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 100, 120, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
                new CustomeThreadpoolFactory(), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            }
        });

        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        });
    }
}

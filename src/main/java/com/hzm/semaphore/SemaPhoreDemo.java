package com.hzm.semaphore;

public class SemaPhoreDemo {

    static int count;
    // 初始化信号量
    static final java.util.concurrent.Semaphore s = new java.util.concurrent.Semaphore(1);

    // 用信号量保证互斥
    static void addOne() throws InterruptedException {
        s.acquire();
        try {
            count += 1;
        } finally {
            s.release();
        }
    }

}

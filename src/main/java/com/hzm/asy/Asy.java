package com.hzm.asy;

import javax.xml.ws.Response;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用线程的等待-通知,模拟实现异步转同步
 */
public class Asy {


    private Response response;
    // 创建锁与条件变量
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();

    // 调用方通过该方法等待结果
    Object get(int timeout) throws TimeoutException, InterruptedException {
        long start = System.nanoTime();
        lock.lock();
        try {
            while (!isDone()) {
                done.await(10, TimeUnit.SECONDS);
                long cur = System.nanoTime();
                if (isDone() ||
                        cur - start > timeout) {
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
        if (!isDone()) {
            throw new TimeoutException();
        }
        return response;
    }

    // RPC 结果是否已经返回
    boolean isDone() {
        return response != null;
    }

    // RPC 结果返回时调用该方法
    private void doReceived(Response res) {
        lock.lock();
        try {
            response = res;
            done.signal();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        Asy asy = new Asy();
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

}

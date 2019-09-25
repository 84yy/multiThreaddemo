package com.hzm;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 展示加锁解锁操作
 * happen-before规则保证了变量的可见性
 * 可重入锁的演示
 */
class X {

    private final Lock rtl = new ReentrantLock();
    //如果不加volatile关键字是不是就无法保证可见性了?????
    volatile int value;


    public void addOne() {
        // 获取锁
        rtl.lock();
        try {
            value += 1;
        } finally {
            // 保证锁能释放
            rtl.unlock();
        }
    }

    public void subOne() {
        // 获取锁
        rtl.lock();
        try {
            //可重入锁的演示
            addOne();
            value -= 1;
        } finally {
            // 保证锁能释放
            rtl.unlock();
        }
    }

    public int getValue() {
        return value;
    }


    public static void main(String[] args) {
        X x = new X();
        x.addOne();
        x.subOne();
        System.out.println(x.getValue());
    }
}

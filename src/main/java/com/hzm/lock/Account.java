package com.hzm.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示一个可能引起活锁的情况
 *
 * 也可以通过优先锁定id小的资源
 */
class Account {
    private int balance;
    private final Lock lock = new ReentrantLock();

    //可能存在活锁问题
    void transfer(Account tar, int amt) {
        while (true) {
            if (this.lock.tryLock()) {
                try {
                    if (tar.lock.tryLock()) {
                        try {
                            this.balance -= amt;
                            tar.balance += amt;
                        } finally {
                            tar.lock.unlock();
                        }
                    }//if
                } finally {
                    this.lock.unlock();
                }
            }//if
        }//while
    }//transfer




    //解决活锁的问题
    void transfer2(Account tar, int amt) throws InterruptedException {
        while (true) {
            if (this.lock.tryLock(10, TimeUnit.MICROSECONDS)) {
                try {
                    if (tar.lock.tryLock(10, TimeUnit.MICROSECONDS)) {
                        try {
                            this.balance -= amt;
                            tar.balance += amt;
                        } finally {
                            tar.lock.unlock();
                        }
                    }//if
                } finally {
                    this.lock.unlock();
                }
            }//if
        }//while
    }//transfer
}

package com.hzm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * 展示通过第三方资源调度解决线程死锁的问题
 * 展示wait() notifyAll()的使用,实现等待-通知机制,优化线程的等待
 */
class Tran {
    public static void main(String[] args) throws InterruptedException {
        final Account accountA = new Account(100);
        final Account accountB = new Account(100);
        CountDownLatch countDownLatch = new CountDownLatch(200);
        //模拟账户a给账户b转账
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                accountA.transactionToTarget(1, accountB);
                countDownLatch.countDown();
            }).start();
        }
        //模拟账户b给账户a转账
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                accountB.transactionToTarget(1, accountA);
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println(accountA.getBalance());
        System.out.println(accountB.getBalance());
    }

}

class Account {

    public Account(int balance) {
        this.balance = balance;
    }

    private int balance;

    private void add(int balance) {
        this.balance += balance;
    }

    private void sub(int balance) {
        try {
            Thread.sleep(40);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.balance -= balance;
    }


    public int getBalance() {
        return balance;
    }

    public void transactionToTarget(int i, Account target) {
        Allocate.getInstance().apply(this, target);
        this.sub(i);
        target.add(i);
        Allocate.getInstance().release(this, target);
    }
}

class Allocate {

    private List<Account> lockAccList = new ArrayList<Account>();
    private static final Allocate ALLOCATE = new Allocate();

    private Allocate() {
    }

    public static Allocate getInstance() {
        return ALLOCATE;
    }

    public synchronized void apply(Account source, Account target) {
        while ((lockAccList.contains(source) || lockAccList.contains(target))) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lockAccList.add(source);
        lockAccList.add(target);
    }

    public synchronized void release(Account source, Account target) {
        lockAccList.remove(source);
        lockAccList.remove(target);
        notifyAll();
    }


}

package com.hzm;


/**
 * 展示interrupt()方法的使用
 *
 * 线程的interrupt方法应该由线程自己触发,
 * 调用了该方法只是把线程的interrupt标识位置为true,并不是真正终止线程的执行
 * 如果线程再休眠状态调用了该方法,会抛出InterruptedException异常,抛出该异常会重置interrupt标志位
 *
 *
 * Thread t1 = new Thread( new Runnable(){
 *     public void run(){
 *         // 若未发生中断，就正常执行任务
 *         while(!Thread.currentThread.isInterrupted()){
 *             // 正常任务代码……
 *         }
 *
 *         // 中断的处理代码……
 *         doSomething();
 *     }
 * } ).start();
 *
 * 作者：大闲人柴毛毛
 * 链接：https://www.zhihu.com/question/41048032/answer/252905837
 * 来源：知乎
 *
 *
 *
 *
 */
public class TestInterrupt {

    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //如果不重置interrupt标志位,会导致死循环
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
                System.out.println("线程interrupt状态为false...");
            }
        });

        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
    }
}


package com.hzm.completionservice;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * 演示ExecutorCompletionService的使用
 */
public class CompletionServiceTest {


    /**
     * 最简单的使用,异步获取任务的执行结果,其实也是生产者-消费者模型的一种了
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void test1() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(pool);
        completionService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("t0...");
                return "t0";
            }
        });
        completionService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("t2...");
                return "t2";
            }
        });
        completionService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("t2...");
                return "t2";
            }
        });

        //对执行结果的处理逻辑
        for (int i = 0; i < 3; i++) {
            Future<String> take = completionService.take();
            String s = take.get();
            System.out.println("取到结果" + s + " 准备入库...");
        }

    }


    /**
     * 演示通过completionService实现forking模式,冗余某个服务的多方调用,只要有一个正常返回,则返回
     */
    @Test
    public void testForkingPattern() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(pool);
        ArrayList<Future> futureList = new ArrayList<>();
        Future<String> future0 = completionService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (Math.random() < 0.66) {
                    Thread.sleep(10000);
                }
                return "t0";
            }
        });
        futureList.add(future0);
        Future<String> future1 = completionService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (Math.random() > 0.5) {
                    Thread.sleep(10000);
                }
                return "t1";
            }
        });
        futureList.add(future1);
        Future<String> future2 = completionService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (Math.random() > 0.5) {
                    Thread.sleep(10000);
                }
                return "t2";
            }
        });
        futureList.add(future2);
        try {
            for (; ; ) {
                Future<String> take = completionService.take();
                String s = take.get();
                if (s != null) {
                    System.out.println("成功获得返回 " + s);
                    break;
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            //尝试取消剩余任务的执行
            futureList.forEach(future -> future.cancel(true));
        }

    }
}

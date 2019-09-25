package com.hzm.completablefuture;

import org.junit.Test;

import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 关于线程池
 * http://songkun.me/2018/12/03/2018-12-3-java-completablfuture-drawbacks/
 */
@SuppressWarnings("all")
public class CompletableFutureTest {


    /**
     * 查询completablefuture中任务的串行执行
     */
    @Test
    public void test1() {
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).thenRunAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务2");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).thenRunAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务3");
            }
        });
        completableFuture.join();
    }

    /**
     * 测试completablefuture的汇聚执行
     */
    @Test
    public void test2() {
        CompletableFuture<String> f0 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("任务0开始...");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务0完成...");
                return "t0";
            }
        });

        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("任务1开始...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务1完成...");
                return "t1";
            }
        });


        CompletableFuture<Void> f2 = f1.runAfterBoth(f1, new Runnable() {
            @Override
            public void run() {
                System.out.println("任务2开始...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务2完成...");
            }
        });

        f2.join();

    }


    /**
     * 测试completablefuture的or执行
     */
    @Test
    public void test3() {
        CompletableFuture<String> f0 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("任务0开始...");
                try {
                    if (Math.random() < 0.5D) {
                        Thread.sleep(200);
                    } else {
                        Thread.sleep(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务0完成...");
                return "t0";
            }
        });

        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("任务1开始...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务1完成...");
                return "t1";
            }
        });


        /**
         * 汇聚执行的任务
         */
        CompletableFuture<Object> f2 = f0.applyToEither(f1, new Function<String, Object>() {
            @Override
            public Object apply(String preResult) {
                System.out.println("先完成的是" + preResult + "任务2开始...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务2完成...");
                return "t1";
            }
        });

        f2.join();

    }


    /**
     * 测试completablefuture中使用独立的线程池,而不是默认的 ForkJoinPool.commonPool()
     * commonPool的默认线程数书cpu核心数
     * 关于默认线程池的说明 http://songkun.me/2018/12/03/2018-12-3-java-completablfuture-drawbacks/
     */
    @Test
    public void test4() {
        int processorAmount = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                processorAmount,
                processorAmount * 2 + 1,
                100,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.DiscardPolicy()
        );

        CompletableFuture<String> f0 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("任务0开始...");
                try {
                    if (Math.random() < 0.5D) {
                        Thread.sleep(200);
                    } else {
                        Thread.sleep(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务0完成...");
                return "t0";
            }
        }, pool);

        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("任务1开始...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务1完成...");
                return "t1";
            }
        }, pool);


        /**
         * 汇聚执行的任务
         */
        CompletableFuture<Object> f2 = f0.applyToEither(f1, new Function<String, Object>() {
            @Override
            public Object apply(String preResult) {
                System.out.println("先完成的是" + preResult + "任务2开始...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务2完成...");
                return "t1";
            }
        });

        f2.join();
    }


    /**
     * 异常处理
     */
    @Test
    public void test5() {
        int processorAmount = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                processorAmount,
                processorAmount * 2 + 1,
                100,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.DiscardPolicy()
        );

        CompletableFuture<String> f0 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("任务0开始...");
                try {
                    if (Math.random() < 0.5D) {
                        //模拟发生非受检异常
                        int i = 1 / 0;
                    } else {
                        Thread.sleep(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务0完成...");
                return "t0";
            }
        }, pool).exceptionally(new Function<Throwable, String>() {
            @Override
            public String apply(Throwable throwable) {
                System.out.println("执行t0发生异常,进行降级处理...");
                return "t0";
            }
        });

        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("任务1开始...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务1完成...");
                return "t1";
            }
        }, pool);


        /**
         * 汇聚执行的任务
         */
        CompletableFuture<Object> f2 = f0.applyToEither(f1, new Function<String, Object>() {
            @Override
            public Object apply(String preResult) {
                System.out.println("先完成的是" + preResult + "任务2开始...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务2完成...");
                return "t1";
            }
        });

        f2.join();
    }
}

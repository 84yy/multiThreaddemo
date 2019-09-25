package com.hzm.forkjoinpool;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 演示forkjoinpool计算斐波那契数列 1,1,2,3,5,8,13
 */
public class ForkJoinPoolTest {
    public static void main(String[] args) {
        // 创建分治任务线程池
        ForkJoinPool fjp = new ForkJoinPool(4);
        // 创建分治任务
        Fibonacci fib = new Fibonacci(7);
        // 启动分治任务
        Integer result = fjp.invoke(fib);
        // 输出结果
        System.out.println(result);
        System.out.println(recursiveFibonacci(7));
    }

    // 递归任务
    static class Fibonacci extends RecursiveTask<Integer> {

        final int n;

        Fibonacci(int n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if (n <= 1) {
                return n;
            }
            Fibonacci f1 = new Fibonacci(n - 1);
            // 创建子任务
            f1.fork();
            Fibonacci f2 = new Fibonacci(n - 2);
            // 等待子任务结果，并合并结果
            return f2.compute() + f1.join();
        }
    }

    /**
     * 普通的递归计算斐波那契数列
     * 公式: f(n) = f(n-1) + f(n-2)
     * 终止条件 n = 1
     *
     * @param num
     * @return
     */
    public static int recursiveFibonacci(int num) {
        if (num <= 2) {
            return 1;
        }
        return recursiveFibonacci(num - 1) + recursiveFibonacci(num - 2);
    }

}

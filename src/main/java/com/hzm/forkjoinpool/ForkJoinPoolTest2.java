package com.hzm.forkjoinpool;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 模拟是哟个forkjoinpool来实现统计文章中单次的出现次数
 * 思路: 把整个文章fork成一行行,分别统计该行的单次,把每行的结果join成最终结果
 */
@SuppressWarnings("all")
public class ForkJoinPoolTest2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //使用数组模拟需要统计的文章
        String[] stringArr = {
                "hello me",
                "no me",
                "hello me",
                "hi he",
                "good she",
                "good she",
                "good she",
                "good she",
                "yes i"
        };
        FJTask fJTask = new FJTask(stringArr, 0, stringArr.length);
        ForkJoinPool forkJoinPool = new ForkJoinPool(3);
        forkJoinPool.execute(fJTask);
        Map<String, Integer> map = fJTask.get();
        System.out.println("统计完成: " + map);
    }


    static class FJTask extends RecursiveTask<Map<String, Integer>> {

        private String[] strArr;
        private int begin;
        private int end;

        public FJTask(String[] strArr, int begin, int end) {
            this.strArr = strArr;
            this.begin = begin;
            this.end = end;
        }

        //任务的切分逻辑
        @Override
        protected Map<String, Integer> compute() {
            if (end - begin == 1) {
                //计算
                return cal(strArr[begin]);
            } else {
                int mid = (begin + end) / 2;
                FJTask fJTask0 = new FJTask(strArr, begin, mid);
                fJTask0.fork();
                FJTask fJTask1 = new FJTask(strArr, mid, end);
                return merge(fJTask1.compute(), fJTask0.join());
            }
        }

        private Map<String, Integer> merge(Map<String, Integer> map1, Map<String, Integer> map2) {
            Map<String, Integer> result = new HashMap<>();
            result.putAll(map1);
            map2.forEach((k, v) -> {
                Integer c = result.get(k);
                if (c != null) {
                    result.put(k, c + v);
                } else {
                    result.put(k, v);
                }
            });
            return result;
        }

        private Map<String, Integer> cal(String s) {
            String[] split = s.trim().split("\\s");
            Map<String, Integer> result = new HashMap<>();
            for (String str : split) {
                Integer integer = result.get(result);
                if (integer != null) {
                    integer++;
                    result.put(str, integer);
                } else {
                    result.put(str, 1);
                }
            }
            return result;
        }
    }
}

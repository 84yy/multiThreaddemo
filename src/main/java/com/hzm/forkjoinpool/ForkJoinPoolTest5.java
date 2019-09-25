package com.hzm.forkjoinpool;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@SuppressWarnings("all")
public class ForkJoinPoolTest5 {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        int num = 1000;
        Integer[] integers = new Integer[num];
        for (int i = 0; i < num; i++) {
            integers[i] = new Double(Math.random() * 1000).intValue();
        }
//        Integer[] sort = sort(integers);
//        for (int i : sort) {
//            System.out.println(i);
//        }
        ForkJoinPool pool = new ForkJoinPool();
        SortTask sortTask = new SortTask(integers);
        pool.execute(sortTask);
        Integer[] integers1 = sortTask.get();
        for (int i : integers1) {
            System.out.println(i);
        }
    }

    public Integer[] sort(Integer[] ints) {
        Integer length = ints.length;
        if (length < 2) {
            return ints;
        }
        int mid = length / 2;
        Integer[] leftArr = Arrays.copyOfRange(ints, 0, mid);
        Integer[] rightArr = Arrays.copyOfRange(ints, mid, length);
        return merge(sort(leftArr), sort(rightArr));
    }

    /*
        到这一步实际左右数组都是只有一个元素了
     */
    private static Integer[] merge(Integer[] leftArr, Integer[] rightArr) {
        Integer[] mergeArr = new Integer[leftArr.length + rightArr.length];
        for (int i = 0, j = 0, k = 0; k < mergeArr.length; k++) {
            if (i >= leftArr.length) {
                mergeArr[k] = rightArr[j++];
            } else if (j >= rightArr.length) {
                mergeArr[k] = leftArr[i++];
            } else if (leftArr[i] > rightArr[j]) {
                mergeArr[k] = rightArr[j++];
            } else {
                mergeArr[k] = leftArr[i++];
            }
        }
        return mergeArr;
    }


    static class SortTask extends RecursiveTask<Integer[]> {

        private Integer[] arr;

        public SortTask(Integer[] arr) {
            this.arr = arr;

        }

        @Override
        protected Integer[] compute() {
            if (arr.length < 2) {
                return arr;
            }
            int mid = arr.length / 2;
            SortTask leftTask = new SortTask(Arrays.copyOfRange(arr, 0, mid));
            leftTask.fork();
            SortTask rightTask = new SortTask(Arrays.copyOfRange(arr, mid, arr.length));
            return merge(rightTask.compute(), leftTask.join());
        }

        private Integer[] merge(Integer[] leftArr, Integer[] rightArr) {
            Integer[] mergeArr = new Integer[leftArr.length + rightArr.length];
            for (int i = 0, j = 0, k = 0; k < mergeArr.length; k++) {
                if (i >= leftArr.length) {
                    mergeArr[k] = rightArr[j++];
                } else if (j >= rightArr.length) {
                    mergeArr[k] = leftArr[i++];
                } else if (leftArr[i] > rightArr[j]) {
                    mergeArr[k] = rightArr[j++];
                } else {
                    mergeArr[k] = leftArr[i++];
                }
            }
            return mergeArr;
        }

    }
}

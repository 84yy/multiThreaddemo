package com.hzm.forkjoinpool;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

@SuppressWarnings("all")
public class ForkJoinPoolTest5 {

    @Test
    public void test() {
        int num = 1000;
        int[] integers = new int[num];
        for (int i = 0; i < num; i++) {
            integers[i] = new Double(Math.random() * 1000).intValue();
        }
        int[] sort = sort(integers);
        for (int i : sort) {
            System.out.println(i);
        }
    }

    public int[] sort(int[] ints) {
        int length = ints.length;
        if (length < 2) {
            return ints;
        }
        int mid = length / 2;
        int[] leftArr = Arrays.copyOfRange(ints, 0, mid);
        int[] rightArr = Arrays.copyOfRange(ints, mid, length);
        return merge(sort(leftArr), sort(rightArr));
    }

    /*
        到这一步实际左右数组都是只有一个元素了
     */
    private int[] merge(int[] leftArr, int[] rightArr) {
        int[] mergeArr = new int[leftArr.length + rightArr.length];
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

        @Override
        protected Integer[] compute() {
            return new Integer[0];
        }
    }
}

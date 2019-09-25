package com.hzm.forkjoinpool;


import java.util.concurrent.*;

/**
 * 使用forkjoingpool来实现快速排序
 */
@SuppressWarnings("all")
public class ForkJoinPoolTest4 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Integer[] integers = {9, 8, 7, 10, 3, 4, 2};
        SortTask sortTask = new SortTask(integers, 0, integers.length-1);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.execute(sortTask);
        Integer[] sortedArr = sortTask.get();
        for (Integer integer : sortedArr) {
            System.out.println(integer);
        }
    }


    static class SortTask extends RecursiveTask<Integer[]> {

        private Integer[] arr;
        private int begin;
        private int end;

        public SortTask(Integer[] arr, int begin, int end) {
            this.arr = arr;
            this.begin = begin;
            this.end = end;
        }

        @Override
        protected Integer[] compute() {
            if (end - begin <= 2) {
                if (end - begin == 1) {
                    Integer[] ints = new Integer[1];
                    ints[0] = arr[begin];
                    return ints;
                } else if (arr[begin] < arr[end]) {
                    Integer[] ints = new Integer[2];
                    ints[0] = arr[begin];
                    ints[1] = arr[end];
                    return ints;
                } else {
                    Integer[] ints = new Integer[2];
                    ints[0] = arr[end];
                    ints[1] = arr[begin];
                    return ints;
                }
                //对两个数进行排序
            } else {
                int mid = (begin + end) / 2;
                SortTask leftTask = new SortTask(arr, begin, mid-1);
                leftTask.fork();
                SortTask rightTask = new SortTask(arr, mid, end);
                return merge(rightTask.compute(), leftTask.join());
            }
        }

        private Integer[] merge(Integer[] leftArr, Integer[] rightArr) {
            for (Integer integer : leftArr) {
                System.out.println("left"+integer);
            }
            for (Integer integer : rightArr) {
                System.out.println("right"+integer);
            }
            int totalLength = leftArr.length + rightArr.length;
            Integer[] mergeArr = new Integer[totalLength];
            int leftIndex = 0;
            int rightIndex = 0;
            for (int i = 0; i < totalLength; i++) {
                if (leftArr[leftIndex] < rightArr[rightIndex]) {
                    mergeArr[i] = leftArr[leftIndex];
                    if (leftArr.length - 1 > leftIndex) {
                        leftIndex++;
                    }
                } else {
                    mergeArr[i] = rightArr[rightIndex];
                    if (rightArr.length - 1 > rightIndex) {
                        rightIndex++;
                    }
                }
            }
            return mergeArr;
        }
    }
}

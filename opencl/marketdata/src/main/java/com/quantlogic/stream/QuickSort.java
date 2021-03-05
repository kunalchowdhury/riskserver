package com.quantlogic.stream;

import com.quantlogic.marketdata.A;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLongArray;

public class QuickSort {

    private static int partition(AtomicLongArray arr, int lo, int hi) {
        int i = lo;
        int j = hi + 1;
        while (true) {
            while (arr.get(++i) < arr.get(lo)) {
                if (i == hi)
                    break;
            }

            while (arr.get(lo) < arr.get(--j)) {
                if (j == lo)
                    break;
            }
            if (i >= j)
                break;
            swap(arr, i, j);

        }
        swap(arr, lo, j);
        return j;
    }

    private static void swap(AtomicLongArray pq, int i, int j) {
        long tmp = pq.get(i - 1);
        pq.set(i - 1, pq.get(j - 1));
        pq.set(j - 1, tmp);
    }

    private static void sort(AtomicLongArray arr){
        sort(arr, 0, arr.length() -1);
    }

    private static int indexOf(AtomicLongArray arr, long key){
        int lo = 0;
        int hi = arr.length() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int timestamp = (int)arr.get(mid) == 0 ? Integer.MAX_VALUE : (int)arr.get(mid) ;
            if      (key < timestamp) hi = mid - 1;
            else if (key > timestamp) lo = mid + 1;
            else return mid;
        }
        return -(lo +1);
    }



    public static void main(String[] args) {
        AtomicLongArray arr=  new AtomicLongArray(9);
        long key = 1100;
        long ver = 10;
        long timestamp = 31;
        long first = key << 48 | ver << 32 | timestamp;
        System.out.println(first);
        System.out.println("timestamp = "+ (int)timestamp);
        System.out.println("ver = "+ ((first >> 32) & 0xFFFF));
        System.out.println("key = "+ (int)(first >> 48));

        arr.set(0, 5);
        arr.set(1, 8);
        arr.set(2, 10);
        System.out.println(indexOf(arr, 5));
    }

    private static void sort(AtomicLongArray arr, int lo, int hi){
        if(hi <= lo){
            return;
        }
        int j = partition(arr, lo, hi);
        sort(arr, lo, j-1);
        sort(arr, j +1, hi);
    }

}

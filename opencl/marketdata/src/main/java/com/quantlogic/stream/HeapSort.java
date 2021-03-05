package com.quantlogic.stream;

import java.util.concurrent.atomic.AtomicLongArray;

public class HeapSort {
    public static void sort(AtomicLongArray pq) {
        int n = pq.length();

        // heapify phase
        for (int k = n/2; k >= 1; k--)
            sink(pq, k, n);

        // sortdown phase
        int k = n;
        while (k > 1) {
            exch(pq, 1, k--);
            sink(pq, 1, k);
        }
    }

    private static void sink(AtomicLongArray pq, int k, int n) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && less(pq, j, j+1)) j++;
            if (!less(pq, k, j)) break;
            exch(pq, k, j);
            k = j;
        }
    }

    private static boolean less(AtomicLongArray pq, int i, int j) {
        return pq.get(i-1) < pq.get(j-1) ;
    }

    private static void exch(AtomicLongArray pq, int i, int j) {
        long tmp = pq.get(i - 1);
        pq.set(i-1, pq.get(j-1));
        pq.set(j-1, tmp);
    }

}

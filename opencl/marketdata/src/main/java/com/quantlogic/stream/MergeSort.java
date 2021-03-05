package com.quantlogic.stream;

import java.util.Arrays;
import java.util.Objects;

public class MergeSort {
    static class MutableInteger{
        int idx;
        public MutableInteger() {}
        public void setIdx(int idx) {
            this.idx = idx;
        }
    }

    private static boolean done= false;
    private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi, Integer target, MutableInteger val) {
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        // merge back to a[]
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)              a[k] = aux[j++];
            else if (j > hi)               a[k] = aux[i++];
            else if (less(aux[j], aux[i])) a[k] = aux[j++];
            else                           a[k] = aux[i++];
        }
    }
    // mergesort a[lo..hi] using auxiliary array aux[lo..hi]
    private static void sort(Comparable[] a, Comparable[] aux, int lo, int hi, Integer target, MutableInteger val) {
        if(done){
           return;
        }
        if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid, target, val);
        sort(a, aux, mid + 1, hi, target, val);
        merge(a, aux, lo, mid, hi, target, val);
        if(lessOrEqal(a[lo], target) && lessOrEqal(target, a[hi])){
            val.idx = hi;
            System.out.println("Done "+hi);
        }
    }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
    public static void sort(Comparable[] a, Integer target, MutableInteger val) {
        Comparable[] aux = new Comparable[a.length];
        sort(a, aux, 0, a.length-1, target, val);
    }
    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    private static boolean lessOrEqal(Comparable v, Comparable w) {
        return v.compareTo(w) < 0 || v.equals(w);
    }


    private static boolean isSorted(Comparable[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    private static boolean isSorted(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(a[i], a[i-1])) return false;
        return true;
    }

    private static void merge(Comparable[] a, int[] index, int[] aux, int lo, int mid, int hi) {

        // copy to aux[]
        for (int k = lo; k <= hi; k++) {
            aux[k] = index[k];
        }

        // merge back to a[]
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)                    index[k] = aux[j++];
            else if (j > hi)                     index[k] = aux[i++];
            else if (less(a[aux[j]], a[aux[i]])) index[k] = aux[j++];
            else                                 index[k] = aux[i++];
        }
    }

    private static void sort(Comparable[] a, int[] index, int[] aux, int lo, int hi, Integer target, MutableInteger val) {
        if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        sort(a, index, aux, lo, mid, target, val);
        sort(a, index, aux, mid + 1, hi, target, val);
        merge(a, index, aux, lo, mid, hi);
    }

    public static void main(String[] args) {
        Integer[] arr = new Integer[]{9, 3, 2, 1, 5, 7, 10};
        Integer target = 2;
        MutableInteger val = new MutableInteger();
        sort(arr, target, val);
        System.out.println(Arrays.toString(arr));
        System.out.println("I found here "+val.idx);

    }

}

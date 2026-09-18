package daa;

import java.util.Objects;

public final class MergeSort {
    private static final int CUTOFF = 15;

    private MergeSort() { }

    public static void sort(int[] a, Metrics metrics) {
        Objects.requireNonNull(a, "Array must not be null");
        Objects.requireNonNull(metrics, "Metrics must not be null");
        metrics.reset();
        long start = System.nanoTime();
        int[] buffer = new int[a.length];
        if (a.length > 0) {
            sort(a, buffer, 0, a.length - 1, 1, metrics);
        }
        metrics.finish(start);
    }

    private static void sort(int[] a, int[] buffer, int low, int high,
                             int depth, Metrics metrics) {
        metrics.recordDepth(depth);
        if (high - low + 1 <= CUTOFF) {
            InsertionSort.sort(a, low, high, metrics);
            return;
        }
        int middle = low + (high - low) / 2;
        sort(a, buffer, low, middle, depth + 1, metrics);
        sort(a, buffer, middle + 1, high, depth + 1, metrics);
        merge(a, buffer, low, middle, high, metrics);
    }

    private static void merge(int[] a, int[] buffer, int low, int middle,
                              int high, Metrics metrics) {
        System.arraycopy(a, low, buffer, low, high - low + 1);
        int left = low;
        int right = middle + 1;
        for (int out = low; out <= high; out++) {
            if (left > middle) {
                a[out] = buffer[right++];
            } else if (right > high) {
                a[out] = buffer[left++];
            } else if (metrics.compare(buffer[left], buffer[right]) <= 0) {
                a[out] = buffer[left++];
            } else {
                a[out] = buffer[right++];
            }
        }
    }
}

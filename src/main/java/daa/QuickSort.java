package daa;

import java.util.Objects;
import java.util.Random;

public final class QuickSort {
    private QuickSort() { }

    public static void sort(int[] a, Metrics metrics) {
        sort(a, metrics, new Random());
    }

    public static void sort(int[] a, Metrics metrics, Random random) {
        Objects.requireNonNull(a, "Array must not be null");
        Objects.requireNonNull(metrics, "Metrics must not be null");
        Objects.requireNonNull(random, "Random generator must not be null");
        metrics.reset();
        long start = System.nanoTime();
        if (a.length > 0) {
            sort(a, 0, a.length - 1, 1, metrics, random);
        }
        metrics.finish(start);
    }

    private static void sort(int[] a, int low, int high, int depth,
                             Metrics metrics, Random random) {
        metrics.recordDepth(depth);
        while (low < high) {
            Partition.EqualRange equal = Partition.split(a, low, high, random, metrics);
            int leftSize = equal.first() - low;
            int rightSize = high - equal.last();
            if (leftSize < rightSize) {
                if (leftSize > 1) {
                    sort(a, low, equal.first() - 1, depth + 1, metrics, random);
                }
                low = equal.last() + 1;
            } else {
                if (rightSize > 1) {
                    sort(a, equal.last() + 1, high, depth + 1, metrics, random);
                }
                high = equal.first() - 1;
            }
        }
    }
}

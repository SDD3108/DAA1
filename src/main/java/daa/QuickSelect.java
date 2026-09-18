package daa;

import java.util.Objects;
import java.util.Random;

public final class QuickSelect {
    private QuickSelect() { }

    public static int select(int[] a, int k) {
        return select(a, k, new Metrics(), new Random());
    }

    public static int select(int[] a, int k, Metrics metrics) {
        return select(a, k, metrics, new Random());
    }

    public static int select(int[] a, int k, Metrics metrics, Random random) {
        if (a == null || a.length == 0) {
            throw new IllegalArgumentException("Array must not be null or empty");
        }
        if (k < 0 || k >= a.length) {
            throw new IllegalArgumentException("k must be in [0, " + (a.length - 1) + "]: " + k);
        }
        Objects.requireNonNull(metrics, "Metrics must not be null");
        Objects.requireNonNull(random, "Random generator must not be null");
        metrics.reset();
        long start = System.nanoTime();
        metrics.recordDepth(1);
        int low = 0;
        int high = a.length - 1;
        while (low < high) {
            Partition.EqualRange equal = Partition.split(a, low, high, random, metrics);
            if (k < equal.first()) {
                high = equal.first() - 1;
            } else if (k > equal.last()) {
                low = equal.last() + 1;
            } else {
                metrics.finish(start);
                return a[k];
            }
        }
        metrics.finish(start);
        return a[low];
    }
}

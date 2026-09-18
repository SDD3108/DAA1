package daa;

import java.util.Random;

final class Partition {
    private Partition() { }

    record EqualRange(int first, int last) { }

    static EqualRange split(int[] a, int low, int high, Random random, Metrics metrics) {
        int pivot = a[low + random.nextInt(high - low + 1)];
        int less = low;
        int current = low;
        int greater = high;
        while (current <= greater) {
            int comparison = metrics.compare(a[current], pivot);
            if (comparison < 0) {
                swap(a, less++, current++);
            } else if (comparison > 0) {
                swap(a, current, greater--);
            } else {
                current++;
            }
        }
        return new EqualRange(less, greater);
    }

    private static void swap(int[] a, int i, int j) {
        int value = a[i];
        a[i] = a[j];
        a[j] = value;
    }
}

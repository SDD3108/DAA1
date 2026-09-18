package daa;

public final class InsertionSort {
    private InsertionSort() { }

    // Inclusive range; called only by MergeSort for at most 15 elements.
    static void sort(int[] a, int low, int high, Metrics metrics) {
        for (int i = low + 1; i <= high; i++) {
            int value = a[i];
            int j = i - 1;
            while (j >= low) {
                if (metrics.compare(a[j], value) <= 0) {
                    break;
                }
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = value;
        }
    }
}

package daa;

import java.util.Arrays;
import java.util.Random;

// Shared assertions: JUnit and the dependency-free runner execute these exact checks.
final class Checks {
    private Checks() { }

    static void randomSorts(boolean merge) {
        Random random = new Random(42);
        for (int trial = 0; trial < 150; trial++) {
            int[] a = new int[random.nextInt(2_001)];
            for (int i = 0; i < a.length; i++) {
                a[i] = trial % 2 == 0 ? random.nextInt() : random.nextInt(10);
            }
            checkSort(a, merge);
        }
    }

    static void edgeSorts(boolean merge) {
        int[][] cases = {{}, {7}, {5, 5, 5, 5}, {-3, -1, 0, 2, 9},
                {9, 2, 0, -1, -3}, {Integer.MAX_VALUE, 0, Integer.MIN_VALUE, -1},
                {2, 1}, {1, 2}, {1, 1}};
        for (int[] a : cases) {
            checkSort(a, merge);
        }
        for (int n : new int[]{14, 15, 16, 29, 30, 31, 32}) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = n - i;
            checkSort(a, merge);
        }
    }

    static void quickSortDepth() {
        int n = 100_000;
        for (int seed = 0; seed < 10; seed++) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = i;
            Metrics metrics = new Metrics();
            QuickSort.sort(a, metrics, new Random(seed));
            require(metrics.maxDepth() <= 2 * Math.log(n) / Math.log(2), "Depth bound failed");
            for (int i = 0; i < n; i++) require(a[i] == i, "Sorted input corrupted");
        }
    }

    static void equalValues() {
        int[] a = new int[100_000];
        Arrays.fill(a, 7);
        Metrics metrics = new Metrics();
        QuickSort.sort(a, metrics, new Random(1));
        require(metrics.comparisons() == a.length, "Equal array should take one partition");
        require(metrics.maxDepth() == 1, "Equal array should not recurse");
        require(QuickSelect.select(a, 50_000, metrics, new Random(2)) == 7, "Equal select failed");
        require(metrics.comparisons() == a.length && metrics.maxDepth() == 1,
                "Equal select should take one partition");
    }

    static void randomSelect() {
        Random random = new Random(73);
        for (int trial = 0; trial < 120; trial++) {
            int[] a = new int[1 + random.nextInt(150)];
            for (int i = 0; i < a.length; i++) {
                a[i] = trial % 2 == 0 ? random.nextInt() : random.nextInt(10);
            }
            int[] expected = a.clone();
            Arrays.sort(expected);
            for (int k = 0; k < a.length; k++) {
                Metrics metrics = new Metrics();
                int actual = QuickSelect.select(a.clone(), k, metrics, new Random(trial * 1000L + k));
                require(actual == expected[k], "Wrong selection at rank " + k);
                require(metrics.maxDepth() == 1, "Iterative selection must use constant stack");
            }
        }
    }

    static void edgeSelect() {
        int[][] cases = {{7}, {3, 3, 3}, {-9, -1, 0, 3, 5}, {9, 3, 0, -5},
                {Integer.MIN_VALUE, Integer.MAX_VALUE, 0, Integer.MIN_VALUE}};
        for (int[] a : cases) {
            int[] expected = a.clone();
            Arrays.sort(expected);
            for (int k = 0; k < a.length; k++) {
                require(QuickSelect.select(a.clone(), k) == expected[k], "Edge selection failed");
            }
        }
    }

    static void invalidSelect() {
        expectIllegal(() -> QuickSelect.select(new int[0], 0));
        expectIllegal(() -> QuickSelect.select(new int[]{1}, -1));
        expectIllegal(() -> QuickSelect.select(new int[]{1}, 1));
        expectIllegal(() -> QuickSelect.select(null, 0));
    }

    static void metricsReset() {
        Metrics metrics = new Metrics();
        MergeSort.sort(new int[]{3, 2, 1}, metrics);
        require(metrics.comparisons() == 3, "Insertion counter mismatch");
        MergeSort.sort(new int[0], metrics);
        require(metrics.comparisons() == 0 && metrics.maxDepth() == 0, "Merge metrics not reset");
        QuickSort.sort(new int[]{3, 2, 1}, metrics, new Random(1));
        QuickSort.sort(new int[0], metrics, new Random(1));
        require(metrics.comparisons() == 0 && metrics.maxDepth() == 0, "Quick metrics not reset");
        QuickSelect.select(new int[]{3, 2, 1}, 1, metrics, new Random(1));
        QuickSelect.select(new int[]{7}, 0, metrics, new Random(1));
        require(metrics.comparisons() == 0 && metrics.maxDepth() == 1, "Select metrics not reset");
        require(metrics.elapsedNanos() >= 0, "Invalid time");
    }

    static void partitionInvariant() {
        Random random = new Random(97);
        for (int trial = 0; trial < 100; trial++) {
            int[] a = new int[3 + random.nextInt(200)];
            for (int i = 0; i < a.length; i++) a[i] = random.nextInt(20) - 10;
            int first = a[0];
            int last = a[a.length - 1];
            int[] before = a.clone();
            Metrics metrics = new Metrics();
            Partition.EqualRange equal = Partition.split(a, 1, a.length - 2, random, metrics);
            int pivot = a[equal.first()];
            for (int i = 1; i < equal.first(); i++) require(a[i] < pivot, "Invalid left partition");
            for (int i = equal.first(); i <= equal.last(); i++) require(a[i] == pivot, "Invalid equal partition");
            for (int i = equal.last() + 1; i < a.length - 1; i++) require(a[i] > pivot, "Invalid right partition");
            require(a[0] == first && a[a.length - 1] == last, "Partition changed outside range");
            require(metrics.comparisons() == a.length - 2, "Partition comparison counter mismatch");
            Arrays.sort(before);
            Arrays.sort(a);
            require(Arrays.equals(a, before), "Partition lost elements");
        }
    }

    static void adversarialPivots() {
        Random smallestPivot = new Random(0) {
            @Override public int nextInt(int bound) { return 0; }
        };
        int n = 2_000;
        int[] descending = new int[n];
        for (int i = 0; i < n; i++) descending[i] = n - i;
        Metrics metrics = new Metrics();
        QuickSort.sort(descending, metrics, smallestPivot);
        for (int i = 0; i < n; i++) require(descending[i] == i + 1, "Adversarial sort failed");
        require(metrics.maxDepth() == 1, "Large partitions must be handled by the loop");
        require(metrics.comparisons() == (long) n * (n + 1) / 2 - 1, "Expected quadratic pivot sequence");
    }

    private static void checkSort(int[] source, boolean merge) {
        int[] a = source.clone();
        int[] expected = source.clone();
        Arrays.sort(expected);
        if (merge) MergeSort.sort(a, new Metrics());
        else QuickSort.sort(a, new Metrics(), new Random(11));
        require(Arrays.equals(a, expected), "Sort mismatch");
    }

    private static void expectIllegal(Runnable operation) {
        try {
            operation.run();
        } catch (IllegalArgumentException exception) {
            require(exception.getMessage() != null && !exception.getMessage().isBlank(),
                    "Exception needs a clear message");
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}

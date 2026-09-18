package daa;

public final class VerificationMain {
    private VerificationMain() { }

    public static void main(String[] args) {
        check("MergeSort: 150 random arrays", () -> Checks.randomSorts(true));
        check("QuickSort: 150 random arrays", () -> Checks.randomSorts(false));
        check("MergeSort: edge and cutoff cases", () -> Checks.edgeSorts(true));
        check("QuickSort: edge and cutoff cases", () -> Checks.edgeSorts(false));
        check("QuickSort: sorted 100000, ten seeds, depth bound", Checks::quickSortDepth);
        check("Equal arrays: linear comparisons and constant depth", Checks::equalValues);
        check("QuickSelect: 120 random arrays, every rank", Checks::randomSelect);
        check("QuickSelect: edge cases", Checks::edgeSelect);
        check("QuickSelect: invalid inputs", Checks::invalidSelect);
        check("Metrics: reset and known counters", Checks::metricsReset);
        check("Partition: 100 invariant and permutation checks", Checks::partitionInvariant);
        check("Adversarial pivots: quadratic work, constant stack", Checks::adversarialPivots);
        System.out.println("PASS: 12/12 shared check groups. This runner does not use the JUnit engine.");
    }

    private static void check(String name, Runnable check) {
        check.run();
        System.out.println("PASS: " + name);
    }
}

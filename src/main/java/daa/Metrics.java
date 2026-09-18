package daa;

public final class Metrics {
    private long comparisons;
    private int maxDepth;
    private long elapsedNanos;

    public void reset() {
        comparisons = 0;
        maxDepth = 0;
        elapsedNanos = 0;
    }

    public int compare(int left, int right) {
        comparisons++;
        return Integer.compare(left, right);
    }

    public void recordDepth(int depth) {
        maxDepth = Math.max(maxDepth, depth);
    }

    public void finish(long startedAt) {
        elapsedNanos = System.nanoTime() - startedAt;
    }

    public long comparisons() { return comparisons; }
    public int maxDepth() { return maxDepth; }
    public long elapsedNanos() { return elapsedNanos; }
    public double timeMillis() { return elapsedNanos / 1_000_000.0; }
}

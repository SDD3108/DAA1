package daa;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;

public final class Benchmark {
    private static final int[] SIZES = {1_000, 10_000, 100_000, 1_000_000};
    private static final String[] INPUTS = {"random", "sorted", "duplicates"};
    private static final String[] ALGORITHMS = {"MergeSort", "QuickSort", "QuickSelect"};
    private static final int RUNS = 5;
    private static final int WARMUPS = 2;
    private static final long DATA_SEED = 2525L;
    private static final long PIVOT_SEED = 3108L;

    private Benchmark() { }

    public static void main(String[] args) throws IOException {
        Path output = Path.of(args.length == 0 ? "." : args[0]);
        Files.createDirectories(output);
        try (PrintWriter summary = writer(output.resolve("results.csv"));
             PrintWriter raw = writer(output.resolve("results_raw.csv"))) {
            summary.println("algorithm,input,n,time_ms,comparisons,max_depth");
            raw.println("algorithm,input,n,run,data_seed,pivot_seed,time_ns,time_ms,comparisons,max_depth");
            for (int inputIndex = 0; inputIndex < INPUTS.length; inputIndex++) {
                String input = INPUTS[inputIndex];
                for (int n : SIZES) {
                    long dataSeed = DATA_SEED + 31L * n + inputIndex;
                    int[] source = generate(input, n, dataSeed);
                    int[] expected = source.clone();
                    Arrays.sort(expected);
                    for (String algorithm : ALGORITHMS) {
                        for (int warmup = 0; warmup < WARMUPS; warmup++) {
                            run(algorithm, source, expected, PIVOT_SEED - 1 - warmup);
                        }
                        Metrics[] samples = new Metrics[RUNS];
                        for (int run = 0; run < RUNS; run++) {
                            long pivotSeed = PIVOT_SEED + 17L * n + 101L * inputIndex + run;
                            Metrics metrics = run(algorithm, source, expected, pivotSeed);
                            samples[run] = metrics;
                            raw.printf(Locale.ROOT, "%s,%s,%d,%d,%d,%d,%d,%.6f,%d,%d%n",
                                    algorithm, input, n, run + 1, dataSeed, pivotSeed,
                                    metrics.elapsedNanos(), metrics.timeMillis(),
                                    metrics.comparisons(), metrics.maxDepth());
                        }
                        Arrays.sort(samples, Comparator.comparingLong(Metrics::elapsedNanos));
                        Metrics median = samples[RUNS / 2];
                        summary.printf(Locale.ROOT, "%s,%s,%d,%.6f,%d,%d%n",
                                algorithm, input, n, median.timeMillis(),
                                median.comparisons(), median.maxDepth());
                        System.out.printf(Locale.ROOT, "%s / %s / %,d: %.3f ms, depth %d%n",
                                algorithm, input, n, median.timeMillis(), median.maxDepth());
                    }
                }
            }
        }
        try (PrintWriter environment = writer(output.resolve("environment.txt"))) {
            environment.println("Runtime: " + System.getProperty("java.runtime.version"));
            environment.println("VM: " + System.getProperty("java.vm.name"));
            environment.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
            environment.println("Available processors: " + Runtime.getRuntime().availableProcessors());
            environment.println("Maximum heap bytes: " + Runtime.getRuntime().maxMemory());
            environment.println("Measurements: 5 runs; 2 unrecorded warm-ups per case.");
            environment.println("Summary: median-time run, with counters from that same run.");
            environment.println("QuickSelect rank: k = n / 2 (zero-based upper median).");
            environment.println("Input generation, cloning and verification are outside timing.");
            environment.println("Metrics instrumentation and MergeSort buffer allocation are inside timing.");
            environment.println("All warm-up and measured outputs checked against Arrays.sort.");
        }
    }

    private static PrintWriter writer(Path path) throws IOException {
        return new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8));
    }

    private static int[] generate(String input, int n, long seed) {
        int[] a = new int[n];
        Random random = new Random(seed);
        for (int i = 0; i < n; i++) {
            a[i] = input.equals("duplicates") ? random.nextInt(10) : random.nextInt();
        }
        if (input.equals("sorted")) {
            Arrays.sort(a);
        }
        return a;
    }

    private static Metrics run(String algorithm, int[] source, int[] expected, long seed) {
        int[] a = source.clone();
        Metrics metrics = new Metrics();
        Random random = new Random(seed);
        if (algorithm.equals("QuickSelect")) {
            int k = a.length / 2;
            int selected = QuickSelect.select(a, k, metrics, random);
            if (selected != expected[k]) {
                throw new AssertionError("QuickSelect returned an incorrect element");
            }
        } else {
            if (algorithm.equals("MergeSort")) {
                MergeSort.sort(a, metrics);
            } else {
                QuickSort.sort(a, metrics, random);
            }
            if (!Arrays.equals(a, expected)) {
                throw new AssertionError(algorithm + " returned an incorrect array");
            }
        }
        return metrics;
    }
}

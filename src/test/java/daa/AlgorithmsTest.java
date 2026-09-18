package daa;

import org.junit.jupiter.api.Test;

class AlgorithmsTest {
    @Test void mergeSortRandomArrays() { Checks.randomSorts(true); }
    @Test void quickSortRandomArrays() { Checks.randomSorts(false); }
    @Test void mergeSortEdgeCases() { Checks.edgeSorts(true); }
    @Test void quickSortEdgeCases() { Checks.edgeSorts(false); }
    @Test void quickSortBoundedDepth() { Checks.quickSortDepth(); }
    @Test void equalValuesLinearPartition() { Checks.equalValues(); }
    @Test void quickSelectEveryRank() { Checks.randomSelect(); }
    @Test void quickSelectEdgeCases() { Checks.edgeSelect(); }
    @Test void quickSelectInvalidInput() { Checks.invalidSelect(); }
    @Test void metricsResetBetweenCalls() { Checks.metricsReset(); }
    @Test void partitionPreservesInvariants() { Checks.partitionInvariant(); }
    @Test void adversarialPivotsUseBoundedStack() { Checks.adversarialPivots(); }
}

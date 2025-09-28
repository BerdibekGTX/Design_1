package Satosh1;

import java.util.ArrayList;
import java.util.Arrays;

public class Deterministic_Select {
    public static Metrics_Tracker lastRunMetrics;

    public static int deterministicSelect(int[] arr, int k) {
        Metrics_Tracker metrics = new Metrics_Tracker();
        lastRunMetrics = metrics;

        if (arr == null || k < 0 || k >= arr.length) {
            throw new IllegalArgumentException("Invalid input array or k");
        }

        int[] arrCopy = Arrays.copyOf(arr, arr.length);
        metrics.incrementAllocationCounter();

        return deterministicSelectHelper(arrCopy, k, 0, arrCopy.length - 1, metrics);
    }

    private static int medianOfMedians(int[] arr, Metrics_Tracker metrics) {
        metrics.increaseRecursionDepth();

        ArrayList<Integer> medians = new ArrayList<>();
        metrics.incrementAllocationCounter();

        for (int i = 0; i < arr.length; i += 5) {
            int end = Math.min(i + 5, arr.length);
            int[] group = Arrays.copyOfRange(arr, i, end);
            metrics.incrementAllocationCounter();
            Arrays.sort(group);
            medians.add(group[group.length / 2]);
        }

        metrics.incrementComparisonCounter();
        if (medians.size() <= 1) {
            metrics.decreaseRecursionDepth();
            return medians.get(0);
        }

        int[] mediansArray = medians.stream().mapToInt(Integer::intValue).toArray();
        metrics.incrementAllocationCounter();

        int result = medianOfMedians(mediansArray, metrics);
        metrics.decreaseRecursionDepth();
        return result;
    }

    private static int deterministicSelectHelper(int[] arr, int k, int left, int right, Metrics_Tracker metrics) {
        metrics.increaseRecursionDepth();

        metrics.incrementComparisonCounter();
        if (left == right) {
            metrics.decreaseRecursionDepth();
            return arr[left];
        }

        int[] subarray = Arrays.copyOfRange(arr, left, right + 1);
        metrics.incrementAllocationCounter();
        int pivot = medianOfMedians(subarray, metrics);

        int pivotIndex = partition(arr, left, right, pivot, metrics);

        metrics.incrementComparisonCounter();
        if (k == pivotIndex) {
            metrics.decreaseRecursionDepth();
            return arr[k];
        } else if (k < pivotIndex) {
            metrics.incrementComparisonCounter();
            int result = deterministicSelectHelper(arr, k, left, pivotIndex - 1, metrics);
            metrics.decreaseRecursionDepth();
            return result;
        } else {
            int result = deterministicSelectHelper(arr, k, pivotIndex + 1, right, metrics);
            metrics.decreaseRecursionDepth();
            return result;
        }
    }

    private static int partition(int[] arr, int left, int right, int pivot, Metrics_Tracker metrics) {
        int pivotIndex = findPivotIndex(arr, left, right, pivot, metrics);
        if (pivotIndex == -1) return left;
        swap(arr, pivotIndex, right);

        int i = left;
        for (int j = left; j < right; j++) {
            metrics.incrementComparisonCounter();
            if (arr[j] <= pivot) {
                swap(arr, i, j);
                i++;
            }
        }
        swap(arr, i, right);
        return i;
    }

    private static int findPivotIndex(int[] arr, int left, int right, int pivot, Metrics_Tracker metrics) {
        for (int i = left; i <= right; i++) {
            metrics.incrementComparisonCounter();
            if (arr[i] == pivot) {
                return i;
            }
        }
        return -1;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
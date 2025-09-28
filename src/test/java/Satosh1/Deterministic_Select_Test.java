package Satosh1;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class Deterministic_Select_Test {
    private static final String METRICS_FILE_NAME = "target/metrics.csv";

    @BeforeAll
    static void clearMetricsFile() {
        try {
            Files.deleteIfExists(Paths.get(METRICS_FILE_NAME));
        } catch (IOException e) {
            System.err.println("Error while trying to delete metrics file: " + e.getMessage());
        }
    }

    @BeforeAll
    static void warmUpJvm() {
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            int[] dummyArray = new int[10];
            for (int j = 0; j < dummyArray.length; j++) {
                dummyArray[j] = random.nextInt();
            }
            Deterministic_Select.deterministicSelect(dummyArray, 0);
        }
    }

    @BeforeEach
    void resetStaticMetrics() {
        Deterministic_Select.lastRunMetrics = null;
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 50, 100, 500, 1000, 2000})
    public void testDeterministicSelect(int size) throws IOException {
        int[] arr = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(size * 10);
        }
        int k = size / 2;

        long startTime = System.nanoTime();

        int selected = Deterministic_Select.deterministicSelect(arr, k);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        Metrics_Tracker metrics = Deterministic_Select.lastRunMetrics;
        assertNotNull(metrics, "Metrics should have been generated");

        System.out.println("Deterministic Select on " + size + " elements took: " + duration + " nanoseconds.");

        metrics.writeMetricsToCSV(duration, "DeterministicSelect_size_" + size);

        int[] sortedArr = arr.clone();
        Arrays.sort(sortedArr);
        assertEquals(sortedArr[k], selected, "Selected element should be the " + k + "-th element.");
    }
}
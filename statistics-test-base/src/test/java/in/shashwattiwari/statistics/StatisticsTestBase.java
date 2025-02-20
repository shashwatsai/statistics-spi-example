package in.shashwattiwari.statistics;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base test class for testing the Statistics interface implementations.
 */
public abstract class StatisticsTestBase {

    /**
     * Abstract method to create a new instance of the Statistics implementation being tested.
     */
    protected abstract Statistics createStatistics();

    @Test
    public void testNoEvents() {
        Statistics statistics = createStatistics();
        assertEquals(0, statistics.min(), "Minimum value should be 0 when no events are recorded.");
        assertEquals(0, statistics.max(), "Maximum value should be 0 when no events are recorded.");
        assertEquals(0, statistics.mean(), 0.001, "Mean should be 0 when no events are recorded.");
        assertEquals(0, statistics.variance(), 0.001, "Variance should be 0 when no events are recorded.");
    }

    @Test
    public void testSingleEvent() {
        Statistics statistics = createStatistics();
        statistics.event(10);
        assertEquals(10, statistics.min(), "Minimum value should match the single event.");
        assertEquals(10, statistics.max(), "Maximum value should match the single event.");
        assertEquals(10, statistics.mean(), 0.001, "Mean should match the single event.");
        assertEquals(0, statistics.variance(), 0.001, "Variance should be 0 for a single event.");
    }

    @Test
    public void testMultipleEvents() {
        Statistics statistics = createStatistics();
        statistics.event(5);
        statistics.event(15);
        assertEquals(5, statistics.min(), "Minimum value should be the smallest event.");
        assertEquals(15, statistics.max(), "Maximum value should be the largest event.");
        assertEquals(10, statistics.mean(), 0.001, "Mean should be the average of the events.");
        assertEquals(25, statistics.variance(), 0.001, "Variance should be correctly calculated.");
    }

    @Test
    public void testConcurrentUpdates() throws InterruptedException {
        Statistics statistics = createStatistics();

        int numThreads = 10;
        int numEventsPerThread = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int startValue = i * numEventsPerThread + 1;
            final int endValue = startValue + numEventsPerThread;

            executorService.submit(() -> {
                try {
                    for (int j = startValue; j < endValue; j++) {
                        statistics.event(j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        int totalEvents = numThreads * numEventsPerThread;
        int minValue = 1;
        int maxValue = numThreads * numEventsPerThread;
        int totalSum = (maxValue * (maxValue + 1)) / 2;

        float expectedMean = (float) totalSum / totalEvents;
        float sumOfSquares = 0;

        for (int i = 1; i <= maxValue; i++) {
            sumOfSquares += (float) i * i;
        }
        float expectedVariance = sumOfSquares / totalEvents - expectedMean * expectedMean;

        assertEquals(minValue, statistics.min(), "Minimum value is incorrect.");
        assertEquals(maxValue, statistics.max(), "Maximum value is incorrect.");
        assertEquals(expectedMean, statistics.mean(), 0.001, "Mean is incorrect.");
        assertEquals(expectedVariance, statistics.variance(), 0.001, "Variance is incorrect.");
    }
}


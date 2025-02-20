package in.shashwattiwari.statistics;

import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatisticsClient {
    public static void main(String[] args) throws InterruptedException {
        ServiceLoader<Statistics> loader = ServiceLoader.load(Statistics.class);

        for (Statistics statistics : loader) {
            long startMillis = System.currentTimeMillis();
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

            System.out.println("Min: " + statistics.min());
            System.out.println("Max: " + statistics.max());
            System.out.println("Max: " + statistics.mean());
            System.out.println("Max: " + statistics.variance());

            // benchmarking
            // ToDo use JMH for micro benchmarking
            String msg = "Implementation %s, took %s ms";
            System.out.println(String.format(msg ,statistics.getClass().getName(), System.currentTimeMillis() - startMillis));
        }
    }
}


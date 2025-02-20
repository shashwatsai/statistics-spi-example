package in.shashwattiwari.statistics;


import java.util.concurrent.atomic.AtomicReference;

/**
 * {@code AtomicThreadSafeStatistics} provides a thread-safe implementation of the {@link Statistics} interface.
 *
 * <p>It leverages Compare-And-Swap (CAS) operations using {@link java.util.concurrent.atomic.AtomicInteger} and
 * {@link java.util.concurrent.atomic.AtomicLong} for efficient and lock-free updates to the minimum, maximum,
 * sum, and count of events. CAS is a low-level atomic operation supported by most modern processors,
 * ensuring thread safety without the need for explicit locking mechanisms.
 *
 * <p>This implementation is designed to perform well under high contention scenarios, where multiple threads
 * concurrently update statistical data. The lock-free approach minimizes thread blocking, making it ideal
 * for use cases requiring high throughput and low latency.
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Thread-safe updates to statistical data (minimum, maximum, mean, variance).</li>
 *   <li>Low-latency operations through CAS-based primitives.</li>
 *   <li>Scalable performance under concurrent access.</li>
 * </ul>
 *
 * <h2>References:</h2>
 * <ul>
 *   <li>Educative Course: <a href="https://www.educative.io/courses/java-multithreading-for-senior-engineering-interviews/atomic-classes">Link</a></li>
 *   <li>Java Atomic classes documentation: {@link java.util.concurrent.atomic}.</li>
 *   <li>Compare-And-Swap operation: <a href="https://en.wikipedia.org/wiki/Compare-and-swap">Wikipedia - CAS</a>.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 *     Statistics stats = new AtomicThreadSafeStatistics();
 *     stats.event(5);
 *     stats.event(10);
 *     System.out.println("Mean: " + stats.mean());
 *     System.out.println("Variance: " + stats.variance());
 * </pre>
 *
 * <h2>Performance Considerations:</h2>
 * While CAS-based implementations provide excellent performance in most scenarios, they may exhibit
 * retry overhead under extremely high contention.
 *
 * @author Shashwat Tiwari
 */

public class AtomicThreadSafeStatistics implements Statistics {

    // Immutable data holder for atomic updates
    private static class Stats {
        final int min;
        final int max;
        final long sum;
        final long sumOfSquares;
        final int count;

        Stats(int min, int max, long sum, long sumOfSquares, int count) {
            this.min = min;
            this.max = max;
            this.sum = sum;
            this.sumOfSquares = sumOfSquares;
            this.count = count;
        }
    }

    // Single atomic reference to hold all stats
    private final AtomicReference<Stats> stats = new AtomicReference<>(new Stats(Integer.MAX_VALUE, Integer.MIN_VALUE, 0, 0, 0));

    @Override
    public void event(int n) {
        stats.updateAndGet(current -> new Stats(
                Math.min(current.min, n),
                Math.max(current.max, n),
                current.sum + n,
                current.sumOfSquares + (long) n * n,
                current.count + 1
        ));
    }

    @Override
    public int min() {
        Stats current = stats.get();
        return current.count == 0 ? 0 : current.min;
    }

    @Override
    public int max() {
        Stats current = stats.get();
        return current.count == 0 ? 0 : current.max;
    }

    @Override
    public float mean() {
        Stats current = stats.get();
        // eventually consistent
        return current.count == 0 ? 0 : (float) current.sum / current.count;
    }

    @Override
    public float variance() {
        Stats current = stats.get();
        if (current.count == 0) return 0;
        // eventually consistent
        float mean = (float) current.sum / current.count;
        return (float) current.sumOfSquares / current.count - mean * mean;
    }
}

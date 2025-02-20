package in.shashwattiwari.statistics;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * {@code LockBasedThreadSafeStatistics} provides a thread-safe implementation of the {@link Statistics} interface.
 *
 * <p>This implementation utilizes a {@link java.util.concurrent.locks.ReentrantReadWriteLock} to separate
 * read and write operations, ensuring high concurrency while maintaining data consistency.
 * By segregating locks for reading and writing, it allows multiple threads to read statistical data concurrently,
 * while write operations are exclusive and block other reads or writes.
 *
 * <h2>Key Design Decisions:</h2>
 * <ul>
 *   <li>Read operations (e.g., {@code min()}, {@code max()}, {@code mean()}, {@code variance()}) acquire the read lock,
 *   enabling multiple readers to access the statistics simultaneously without blocking each other.</li>
 *   <li>Write operations (e.g., {@code event(int n)}) acquire the write lock, ensuring exclusive access
 *   to modify the underlying data structures.</li>
 *   <li>This segregation maximizes throughput by leveraging the high read-to-write ratio typically observed
 *   in statistical systems.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 *     Statistics stats = new LockBasedThreadSafeStatistics();
 *     stats.event(15);
 *     stats.event(25);
 *     System.out.println("Min: " + stats.min());
 *     System.out.println("Mean: " + stats.mean());
 * </pre>
 *
 * <h2>Performance Considerations:</h2>
 * <ul>
 *   <li>Read locks are highly efficient under low write contention, making this implementation ideal for scenarios
 *   with frequent reads and infrequent writes.</li>
 *   <li>Write locks can introduce latency during high write contention, as readers are blocked during write operations.
 *   Proper benchmarking is recommended to assess performance under expected workloads.</li>
 * </ul>
 *
 * <h2>References:</h2>
 * <ul>
 *   <li>Java ReadWriteLock documentation: {@link java.util.concurrent.locks.ReentrantReadWriteLock}</li>
 * </ul>
 *
 * <h2>Concurrency Notes:</h2>
 * This implementation prioritizes consistency and correctness over raw performance.
 *
 * @author Shashwat Tiwari
 */

public class LockBasedThreadSafeStatistics implements Statistics {

    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;
    private long sum = 0;
    private long sumOfSquares = 0;
    private int count = 0;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void event(int n) {
        lock.writeLock().lock(); // Use write lock for updates
        try {
            count++;
            sum += n;
            sumOfSquares += (long) n * n;

            if (n < min) {
                min = n;
            }
            if (n > max) {
                max = n;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int min() {
        lock.readLock().lock(); // Use read lock for reading
        try {
            return count == 0 ? 0 : min;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int max() {
        lock.readLock().lock();
        try {
            return count == 0 ? 0 : max;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public float mean() {
        lock.readLock().lock();
        try {
            return count == 0 ? 0 : (float) sum / count;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public float variance() {
        lock.readLock().lock();
        try {
            if (count == 0) return 0;

            long meanSquare = sum * sum / count;
            return (float) (sumOfSquares - meanSquare) / count;
        } finally {
            lock.readLock().unlock();
        }
    }
}

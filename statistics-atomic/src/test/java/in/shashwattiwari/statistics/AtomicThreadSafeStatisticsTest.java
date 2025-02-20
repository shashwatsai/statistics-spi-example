package in.shashwattiwari.statistics;


public class AtomicThreadSafeStatisticsTest extends StatisticsTestBase {
    @Override
    protected Statistics createStatistics() {
        return new AtomicThreadSafeStatistics();
    }
}

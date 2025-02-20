package in.shashwattiwari.statistics;

public class LockBasedThreadSafeStatisticsTest extends StatisticsTestBase {
    @Override
    protected Statistics createStatistics() {
        return new LockBasedThreadSafeStatistics();
    }
}

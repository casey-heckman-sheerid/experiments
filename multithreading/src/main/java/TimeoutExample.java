import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TimeoutExample {
    private static final Logger LOGGER = Logger.getLogger(TimeoutExample.class.getName());
    private static final long SLEEP_DURATION = 10000L;
    private static final long TIMEOUT = 1000L;

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        LOGGER.info("Starting task");
        Future<?> future = executor.submit(TimeoutExample::sleepAway);

        LOGGER.info(String.format("Blocking for %s ms", TIMEOUT));
        future.get(TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private static void sleepAway() {
        try {
            LOGGER.info(String.format("Sleeping for %s ms", SLEEP_DURATION));
            Thread.sleep(SLEEP_DURATION);
        } catch (InterruptedException e) {
            LOGGER.info("Interrupted");
            Thread.currentThread().interrupt();
        }
    }
}

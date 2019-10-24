import java.util.concurrent.atomic.AtomicInteger;

public class RepeatingTaskExample {

    private static final int CORE_POOL_SIZE = 2;
    private static final long DELAY = 500L;
    private static final long START = System.currentTimeMillis();

    private static final AtomicInteger counter = new AtomicInteger();
    private static final int MAX_COUNT = 10;

    public static void main(String[] args) {
        for (int i = 0; i < CORE_POOL_SIZE; i++) {
            new Thread(RepeatingTaskExample::runIt).start();
        }
        for (int i = 0; i < CORE_POOL_SIZE; i++) {
            new Thread(RepeatingTaskExample::runIt2).start();
        }
    }

    private static void runIt() {
        while (true) {
            try {
                doThreadStuff();
            } catch (InterruptedException e) {
                Thread.currentThread()
                      .interrupt();
                break;
            }
        }
    }

    private static void runIt2() {
        try {
            while (true) {
                doThreadStuff();
            }
        } catch (InterruptedException e) {
            Thread.currentThread()
                  .interrupt();
        }
    }

    private static void doThreadStuff() throws InterruptedException {
        outputThreadStatus();
        assertCountBelowMax();
        Thread.sleep(DELAY);
    }

    private static void assertCountBelowMax() {
        if (counter.get() > MAX_COUNT) {
            Thread.currentThread()
                  .interrupt();
        }
    }

    private static void outputThreadStatus() {
        long timeStamp = System.currentTimeMillis() - START;
        int count = counter.incrementAndGet();
        long threadId = Thread.currentThread()
                              .getId();
        System.out.println(String.format("running %s %s %s", threadId, String.valueOf(count), String.valueOf(timeStamp)));
    }

}

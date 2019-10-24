import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Test;

public class ResourceBundleEvaluationTest {

    private static final int NUM_CALLS = 1000000;
    private static final int NUM_BATCHES = 100;

    private static final String BUNDLE_NAME = "bundle";
    private static final String PROPERTY_KEY = "DUMMY";

    private static final long FIVE_MINUTES_IN_MILLIS = 5L * 60L * 1000L;

    private static final ResourceBundle STATIC_RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    private static final ResourceBundle STATIC_RESOURCE_BUNDLE_WITH_CACHING = ResourceBundle.getBundle(BUNDLE_NAME,
                                                                                                       new ResourceBundle.Control() {
                                                                                                           @Override
                                                                                                           public long getTimeToLive(
                                                                                                                   String baseName,
                                                                                                                   Locale locale) {
                                                                                                               return FIVE_MINUTES_IN_MILLIS;
                                                                                                           }
                                                                                                       });

    @Test
    public void evaluate() {
        evaluate(this::consultResourceBundle, "ResourceBundle");
        evaluate(this::consultStaticResourceBundle, "StaticResourceBundle");
        evaluate(this::consultStaticResourceBundleWithCaching, "StaticResourceBundleWithCaching");
    }

    private void evaluate(Runnable runnable, String functionName) {

        List<Integer> timings = new ArrayList<>();
        for (int batchNum = 0; batchNum < NUM_BATCHES; batchNum++) {
            long start = System.currentTimeMillis();
            for (int callNum = 0; callNum < NUM_CALLS; callNum++) {
                runnable.run();
            }
            long end = System.currentTimeMillis();
            timings.add(Math.toIntExact(end - start));
        }

        IntSummaryStatistics summary = timings.stream()
                                              .collect(IntSummaryStatistics::new,
                                                       IntSummaryStatistics::accept,
                                                       IntSummaryStatistics::combine);

        float variance = (summary.getMax() - summary.getMin()) / 2f;
        System.out.println(String.format("%s: %s +/- %s, [%s, %s]",
                                         functionName,
                                         summary.getAverage(),
                                         variance,
                                         summary.getMin(),
                                         summary.getMax()));
    }

    private void consultResourceBundle() {
        consultResourceBundle(ResourceBundle.getBundle(BUNDLE_NAME));
    }

    private void consultStaticResourceBundle() {
        consultResourceBundle(STATIC_RESOURCE_BUNDLE);
    }

    private void consultStaticResourceBundleWithCaching() {
        consultResourceBundle(STATIC_RESOURCE_BUNDLE_WITH_CACHING);
    }

    private void consultResourceBundle(ResourceBundle bundle) {
        bundle.getString(PROPERTY_KEY);
    }
}

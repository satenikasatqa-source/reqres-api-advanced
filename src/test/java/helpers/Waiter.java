package helpers;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public class Waiter {

    public static <T> T waitFor(Supplier<T> action,
                                java.util.function.Predicate<T> condition,
                                Duration timeout,
                                Duration interval) {

        Instant end = Instant.now().plus(timeout);
        T last = null;

        while (Instant.now().isBefore(end)) {
            last = action.get();
            if (condition.test(last)) {
                return last;
            }
            try {
                Thread.sleep(interval.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Wait interrupted", e);
            }
        }
        return last;
    }
}

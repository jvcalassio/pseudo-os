package util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Logger {

    public static void info(final String message) {
        log(message, "INFO");
    }

    public static void debug(final String message) {
        final String profile = System.getProperty("profile");

        if ("debug".equals(profile) ) {
            log(message, "DEBUG");
        }
    }

    private static void log(final String message, final String level) {
        System.out.println(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS) + " [" + level + "] " + message);
    }

}

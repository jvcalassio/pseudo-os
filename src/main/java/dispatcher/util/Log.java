package util;

import java.util.Objects;

public class Log {

    public static void info(final String message) {
        System.out.println("[INFO] " + message);
    }

    public static void debug(final String message) {
        final String profile = System.getProperty("profile");

        if ("debug".equals(profile) ) {
            System.out.println("[DEBUG] " + message);
        }
    }

}

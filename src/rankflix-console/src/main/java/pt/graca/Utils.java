package pt.graca;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static String instantToString(Instant instant) {
        return ZonedDateTime
                .ofInstant(instant, ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getUserHomePath() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            // For Windows
            return System.getenv("APPDATA");
        } else if (os.contains("mac")) {
            // For macOS
            return System.getProperty("user.home") + "/Library/Application Support";
        } else if (os.contains("nix") || os.contains("nux")) {
            // For Linux
            return System.getProperty("user.home") + "/.config";
        } else {
            // Default to user home directory if OS is unrecognized
            return System.getProperty("user.home");
        }
    }
}

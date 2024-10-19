package pt.graca;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class Utils {
    public static String encodePngImage(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String instantToString(Instant instant) {
        return ZonedDateTime
                .ofInstant(instant, ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String localDateToString(LocalDate localDate) {
        if (localDate == null) return "Some day";
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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

package pt.graca.infra.generator.core;

import pt.graca.Utils;
import pt.graca.api.domain.rank.RankedMedia;
import pt.graca.api.domain.rank.RatedMedia;
import pt.graca.infra.generator.RankGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CanvasGenerator extends RankGenerator {
    public String generateRankUrl(RankedMedia ranking, String title) {
        int width = 1920; // Width of the image
        int baseHeight = 1080; // Base height for the image
        int fontSize = 50; // Main title font size
        int lineHeight = fontSize + 20; // Height for each line

        int numberOfMovies = ranking.rankedMedia().size();
        int totalHeight = baseHeight; // Start with a base height

        // Adjust height based on the number of ranked media
        if (numberOfMovies > 0) {
            totalHeight = Math.max(baseHeight, lineHeight * numberOfMovies + 100); // Adding 100 for margins
        }

        BufferedImage bufferedImage = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, totalHeight);

        // Set the main title
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        g2d.drawString(title, 50, 50); // title

        // Set font for movie titles
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, fontSize));

        int currentY = 100;

        for (int i = 0; i < numberOfMovies; i++) {
            RatedMedia media = ranking.rankedMedia().get(i);
            g2d.drawString(generateTextLine(i + 1, media), 50, currentY); // Draw each title
            currentY += lineHeight; // Update Y position
        }

        // Clean up graphics
        g2d.dispose();

        // Encode the buffered image to a PNG format and return
        return Utils.encodePngImage(bufferedImage);
    }
}

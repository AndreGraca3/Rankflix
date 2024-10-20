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
        int padding = 50; // Padding for the image
        int spaceBetweenTitle = 100; // Space between the title and the list of ranked media

        int numberOfMovies = ranking.rankedMedia().size();

        // Calculate content height and total image height
        int contentHeight = lineHeight * numberOfMovies;
        int totalHeight = contentHeight + 2 * padding + spaceBetweenTitle + fontSize;

        // Create the image with dynamic height
        BufferedImage bufferedImage = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, totalHeight);

        // Set antialiasing for smoother text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set font for the title
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        g2d.setColor(Color.BLACK);

        // Calculate title width and center the title horizontally
        FontMetrics titleMetrics = g2d.getFontMetrics();
        int titleWidth = titleMetrics.stringWidth(title);
        int titleX = (width - titleWidth) / 2;

        // Calculate starting Y position for the title
        int currentY = padding + fontSize; // Start Y just after top padding

        // Draw the title centered
        g2d.drawString(title, titleX, currentY);

        // Move Y position for the ranked list (after title and space between title)
        currentY += spaceBetweenTitle;

        // Set font for the ranked titles
        g2d.setFont(new Font("Arial", Font.PLAIN, fontSize));
        FontMetrics textMetrics = g2d.getFontMetrics();

        // Calculate maximum text width for centering all titles
        int maxTextWidth = 0;
        for (int i = 0; i < numberOfMovies; i++) {
            RatedMedia media = ranking.rankedMedia().get(i);
            String rankedText = generateTextLine(i + 1, media);
            int textWidth = textMetrics.stringWidth(rankedText);
            maxTextWidth = Math.max(maxTextWidth, textWidth);
        }

        // Calculate the X position for the ranked titles, so they are centered based on the widest title
        int textX = (width - maxTextWidth) / 2;

        // Draw each ranked media title centered
        for (int i = 0; i < numberOfMovies; i++) {
            RatedMedia media = ranking.rankedMedia().get(i);
            String rankedText = generateTextLine(i + 1, media);
            g2d.drawString(rankedText, textX, currentY);
            currentY += lineHeight; // Move to the next line
        }

        // Clean up graphics
        g2d.dispose();

        // Encode the buffered image to a PNG format and return
        return Utils.encodePngImage(bufferedImage);
    }
}

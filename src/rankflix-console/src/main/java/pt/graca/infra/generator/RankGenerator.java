package pt.graca.infra.generator;

import pt.graca.api.domain.rank.RankedMedia;
import pt.graca.api.domain.rank.RatedMedia;

import java.util.List;

public abstract class RankGenerator {
    public abstract String generateRankUrl(RankedMedia ranking, String title);

    protected String generateTextContent(List<RatedMedia> ranking) {
        return ranking.stream()
                .map(media -> generateTextLine(ranking.indexOf(media) + 1, media))
                .reduce("", (a, b) -> a + b + "\n");
    }


    protected String generateTextLine(int position, RatedMedia media) {
        String title = media.title();
        String rating = String.format("%.2f", media.rating());

        // Calculate available space for the title based on the length of the rating
        int maxTitleLength = 40 - (rating.length() + 5); // 5 spaces for the " (" and the closing parenthesis
        if (title.length() > maxTitleLength) {
            // Truncate title to fit space for the rating
            title = title.substring(0, maxTitleLength - 3) + "...";
        }

        return position + " - " + title + " (" + rating + ")";
    }
}

package pt.graca.infra.generator;

import pt.graca.api.domain.rank.RankedMedia;
import pt.graca.api.domain.rank.RatedMedia;

import java.util.List;

public abstract class RankGenerator {
    public abstract String generateRankUrl(RankedMedia ranking, String title);

    protected String generateTextContent(List<RatedMedia> ranking) {
        return ranking.stream()
                .map(media ->
                        ranking.indexOf(media) + 1 +
                                " - " +
                                media.title() +
                                " (" + String.format("%.2f", media.rating()) + ")"
                )
                .reduce("", (a, b) -> a + b + "\n");
    }
}

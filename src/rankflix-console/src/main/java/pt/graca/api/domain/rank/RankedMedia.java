package pt.graca.api.domain.rank;

import java.util.List;

public record RankedMedia(List<RatedMedia> media, float averageRating, int totalRatings, int totalWatched) {
}

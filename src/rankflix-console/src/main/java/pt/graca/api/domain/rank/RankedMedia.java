package pt.graca.api.domain.rank;

import java.util.List;

public record RankedMedia(List<RatedMedia> rankedMedia, float averageRating, int totalRatings) {
}

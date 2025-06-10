package pt.graca.api.service.results;

public record MediaRatingUpdateResult(
        String mediaId,
    float averageRating,
    int totalRatings
) {
}

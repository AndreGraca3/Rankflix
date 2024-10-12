package pt.graca.api.service.results;

public record MediaRatingUpdateResult(
    int mediaId,
    float averageRating,
    int totalRatings
) {
}

package pt.graca.api.domain.rank;

public record RatedMedia(
        int tmdbId,
        String title,
        float rating
) {
}

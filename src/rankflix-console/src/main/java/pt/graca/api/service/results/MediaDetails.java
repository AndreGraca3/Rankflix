package pt.graca.api.service.results;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.MediaIds;
import pt.graca.api.domain.MediaType;

import java.time.LocalDate;
import java.util.List;

public record MediaDetails(
        MediaIds ids,
        String title,
        String overview,
        List<String> genres,
        @Nullable LocalDate releaseDate,
        String posterUrl,
        Float globalRating,
        MediaType type
) {
}
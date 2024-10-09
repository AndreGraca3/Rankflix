package pt.graca.service.results;

import org.jetbrains.annotations.Nullable;
import pt.graca.domain.MediaIds;
import pt.graca.domain.MediaType;

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
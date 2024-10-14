package pt.graca.api.service.results;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.media.MediaType;

import java.time.LocalDate;

public record MediaDetailsItem(
        int tmdbId,
        String title,
        @Nullable LocalDate releaseDate,
        String posterUrl,
        MediaType type
) {}
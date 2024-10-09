package pt.graca.service.results;

import org.jetbrains.annotations.Nullable;
import pt.graca.domain.MediaType;

import java.time.LocalDate;

public record MediaDetailsItem(
        int tmdbId,
        String title,
        @Nullable LocalDate releaseDate,
        String posterUrl,
        MediaType type
) {}
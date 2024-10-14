package pt.graca.api.service.results;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.media.MediaIds;
import pt.graca.api.domain.media.MediaType;

import java.time.LocalDate;
import java.util.List;

public class MovieDetails extends MediaDetails {
    public MovieDetails(
            MediaIds ids,
            String title,
            String overview,
            List<String> genres,
            LocalDate releaseDate,
            String posterUrl,
            int budget,
            int revenue,
            int runtime,
            Float globalRating,
            MediaType type
    ) {
        super(ids, title, overview, genres, releaseDate, posterUrl, globalRating, type);
        this.budget = budget == 0 ? null : budget;
        this.revenue = revenue == 0 ? null : revenue;
        this.runtime = runtime;
    }

    @Nullable
    public Integer budget;
    @Nullable
    public Integer revenue;
    public int runtime;
}

package pt.graca.api.service.results;

import pt.graca.api.domain.media.MediaIds;
import pt.graca.api.domain.media.MediaType;

import java.time.LocalDate;
import java.util.List;

public class TvShowDetails extends MediaDetails {

    public TvShowDetails(
            MediaIds ids,
            String title,
            String overview,
            List<String> genres,
            LocalDate releaseDate,
            String posterUrl,
            int seasons,
            LocalDate lastEpisodeDate,
            Float globalRating,
            MediaType type
    ) {
        super(ids, title, overview, genres, releaseDate, posterUrl, globalRating, type);
        this.seasons = seasons;
        this.lastEpisodeDate = lastEpisodeDate;
    }

    public int seasons;
    public LocalDate lastEpisodeDate;
}

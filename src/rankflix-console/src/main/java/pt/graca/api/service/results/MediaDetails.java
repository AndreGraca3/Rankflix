package pt.graca.api.service.results;

import pt.graca.api.domain.media.MediaIds;
import pt.graca.api.domain.media.MediaType;

import java.time.LocalDate;
import java.util.List;

public class MediaDetails {

    public MediaDetails(MediaIds ids, String title, String overview, List<String> genres, LocalDate releaseDate, String posterUrl, Float globalRating, MediaType type) {
        this.ids = ids;
        this.title = title;
        this.overview = overview;
        this.genres = genres;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.globalRating = globalRating;
        this.type = type;
    }

    public MediaIds ids;
    public String title;
    public String overview;
    public List<String> genres;
    public LocalDate releaseDate;
    public String posterUrl;
    public Float globalRating;
    public MediaType type;

}


package pt.graca.api.service.external.content;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.core.NamedIdElement;
import info.movito.themoviedbapi.model.core.multi.MultiMovie;
import info.movito.themoviedbapi.model.core.multi.MultiTvSeries;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.model.tv.series.ExternalIds;
import info.movito.themoviedbapi.model.tv.series.TvSeriesDb;
import info.movito.themoviedbapi.tools.TmdbException;
import pt.graca.api.domain.media.MediaIds;
import pt.graca.api.domain.media.MediaType;
import pt.graca.api.service.results.MediaDetails;
import pt.graca.api.service.results.MediaDetailsItem;
import pt.graca.api.service.results.MovieDetails;
import pt.graca.api.service.results.TvShowDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TmdbProvider implements IContentProvider {

    private final TmdbApi tmdbApi = new TmdbApi(System.getenv("RANKFLIX_TMDB_API_KEY"));
    private final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/original";

    @Override
    public MediaDetails getMediaDetailsById(int tmdbId) {
        try {
            MovieDb movie = tmdbApi.getMovies().getDetails(tmdbId, "en");

            return new MovieDetails(
                    new MediaIds(tmdbId, movie.getImdbID()),
                    movie.getTitle(),
                    movie.getOverview(),
                    movie.getGenres().stream().map(NamedIdElement::getName).toList(),
                    parseMediaDate(movie.getReleaseDate()),
                    TMDB_IMAGE_BASE_URL + movie.getPosterPath(),
                    movie.getBudget(),
                    movie.getRevenue().intValue(),
                    movie.getRuntime(),
                    movie.getVoteAverage().floatValue(),
                    MediaType.MOVIE
            );
        } catch (TmdbException e) {
            try {
                TvSeriesDb tvShow = tmdbApi.getTvSeries().getDetails(tmdbId, "en");
                ExternalIds externalIds = tmdbApi.getTvSeries().getExternalIds(tmdbId);

                return new TvShowDetails(
                        new MediaIds(tmdbId, externalIds.getImdbId()),
                        tvShow.getName(),
                        tvShow.getOverview(),
                        tvShow.getGenres().stream().map(NamedIdElement::getName).toList(),
                        parseMediaDate(tvShow.getFirstAirDate()),
                        TMDB_IMAGE_BASE_URL + tvShow.getPosterPath(),
                        tvShow.getNumberOfSeasons(),
                        parseMediaDate(tvShow.getLastAirDate()),
                        tvShow.getVoteAverage().floatValue(),
                        MediaType.TV_SHOW
                );
            } catch (TmdbException ex) {
                return null;
            }
        }
    }

    // Current implementation is very buggy.
    // It will return fewer results than expected.
    @Override
    public List<MediaDetailsItem> searchMediaByName(String query, int page) {
        if (query.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            var media = tmdbApi.getSearch()
                    .searchMulti(query, true, "en", page)
                    .getResults();

            return media.stream()
                    .map(result -> {
                        switch (result) {
                            case MultiMovie movie -> {
                                return new MediaDetailsItem(
                                        movie.getId(),
                                        movie.getTitle(),
                                        parseMediaDate(movie.getReleaseDate()),
                                        TMDB_IMAGE_BASE_URL + movie.getPosterPath(),
                                        MediaType.MOVIE
                                );
                            }

                            case MultiTvSeries tvShow -> {
                                return new MediaDetailsItem(
                                        tvShow.getId(),
                                        tvShow.getName(),
                                        parseMediaDate(tvShow.getFirstAirDate()),
                                        TMDB_IMAGE_BASE_URL + tvShow.getPosterPath(),
                                        MediaType.TV_SHOW
                                );
                            }

                            default -> {
                                return null;
                            }
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        } catch (TmdbException e) {
            return new ArrayList<>();
        }
    }

    private LocalDate parseMediaDate(String date) {
        return date.isEmpty() ? null : LocalDate.parse(date);
    }
}

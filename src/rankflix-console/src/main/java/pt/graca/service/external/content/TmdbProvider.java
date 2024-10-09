package pt.graca.service.external.content;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.core.NamedIdElement;
import info.movito.themoviedbapi.model.core.multi.MultiMovie;
import info.movito.themoviedbapi.model.core.multi.MultiTvSeries;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.model.tv.series.ExternalIds;
import info.movito.themoviedbapi.model.tv.series.TvSeriesDb;
import info.movito.themoviedbapi.tools.TmdbException;
import pt.graca.service.results.MediaDetailsItem;
import pt.graca.domain.MediaIds;
import pt.graca.domain.MediaType;
import pt.graca.service.results.MediaDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class TmdbProvider implements IContentProvider {

    private final TmdbApi tmdbApi = new TmdbApi(System.getenv("RANKFLIX_TMDB_API_KEY"));
    private final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/original";

    @Override
    public MediaDetails getMediaDetails(int tmdbId) {
        try {
            MovieDb movie = tmdbApi.getMovies().getDetails(tmdbId, "en");

            if (movie != null) {
                return new MediaDetails(
                        new MediaIds(tmdbId, movie.getImdbID()),
                        movie.getTitle(),
                        movie.getOverview(),
                        movie.getGenres().stream().map(NamedIdElement::getName).toList(),
                        parseMediaDate(movie.getReleaseDate()),
                        TMDB_IMAGE_BASE_URL + movie.getPosterPath(),
                        movie.getVoteAverage().floatValue(),
                        MediaType.MOVIE
                );
            }

            TvSeriesDb tvShow = tmdbApi.getTvSeries().getDetails(tmdbId, "en");
            ExternalIds externalIds = tmdbApi.getTvSeries().getExternalIds(tmdbId);

            return new MediaDetails(
                    new MediaIds(tmdbId, externalIds.getImdbId()),
                    tvShow.getName(),
                    tvShow.getOverview(),
                    tvShow.getGenres().stream().map(NamedIdElement::getName).toList(),
                    parseMediaDate(tvShow.getFirstAirDate()),
                    TMDB_IMAGE_BASE_URL + tvShow.getPosterPath(),
                    tvShow.getVoteAverage().floatValue(),
                    MediaType.TV_SHOW
            );

        } catch (TmdbException e) {
            throw new NoSuchElementException("Media not found for ID: " + tmdbId);
        }
    }

    // Current implementation is very buggy.
    // It will return duplicates if there are people in TMDB's page 1
    @Override
    public List<MediaDetailsItem> searchMedia(String query, int initialPage) {
        List<MediaDetailsItem> allResults = new ArrayList<>();
        int page = initialPage;
        int pageSize = 20;

        try {
            while (true) {
                var media = tmdbApi.getSearch()
                        .searchMulti(query, true, "en", page)
                        .getResults();

                if (media == null || media.isEmpty()) {
                    break;
                }

                List<MediaDetailsItem> processedResults = media.stream()
                        .map(result -> {
                            if (result instanceof MultiMovie movie) {
                                return new MediaDetailsItem(
                                        movie.getId(),
                                        movie.getTitle(),
                                        parseMediaDate(movie.getReleaseDate()),
                                        TMDB_IMAGE_BASE_URL + movie.getPosterPath(),
                                        MediaType.MOVIE
                                );
                            } else if (result instanceof MultiTvSeries tvShow) {
                                return new MediaDetailsItem(
                                        tvShow.getId(),
                                        tvShow.getName(),
                                        parseMediaDate(tvShow.getFirstAirDate()),
                                        TMDB_IMAGE_BASE_URL + tvShow.getPosterPath(),
                                        MediaType.TV_SHOW
                                );
                            } else {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();

                allResults.addAll(processedResults);

                if (allResults.size() >= pageSize) {
                    break;
                }

                page++;
            }

            return allResults.subList(0, Math.min(pageSize, allResults.size()));

        } catch (TmdbException e) {
            throw new RuntimeException(e);
        }
    }

    private LocalDate parseMediaDate(String date) {
        return date.isEmpty() ? null : LocalDate.parse(date);
    }
}

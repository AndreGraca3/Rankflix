package pt.graca.infra.content;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.core.NamedIdElement;
import info.movito.themoviedbapi.model.core.multi.MultiMovie;
import info.movito.themoviedbapi.model.core.multi.MultiTvSeries;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.model.tv.series.ExternalIds;
import info.movito.themoviedbapi.model.tv.series.TvSeriesDb;
import info.movito.themoviedbapi.tools.TmdbException;
import pt.graca.api.domain.media.Media;
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
import java.util.concurrent.TimeUnit;

public class TmdbProvider implements IContentProvider {

    private final TmdbApi tmdbApi = new TmdbApi(System.getenv("RANKFLIX_TMDB_API_KEY"));
    private final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/original";

    // Cache for media details with media id as the key
    private final Cache<String, MediaDetails> mediaDetailsCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)  // Cache expires after 1 hour
            .maximumSize(1000)  // Max 1000 items in the cache
            .build();

    // Cache for search results with query and page as the key
    private final Cache<String, List<MediaDetailsItem>> searchCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)  // Cache expires after 1 hour
            .maximumSize(1000)  // Max 1000 items in the cache
            .build();

    @Override
    public MediaDetails getMediaDetailsById(String mediaId) {

        var mediaType = Media.getType(mediaId);
        var tmdbId = Integer.parseInt(Media.getExternalId(mediaId));

        return mediaDetailsCache.get(mediaId, id -> {
            try {
                switch (mediaType) {
                    case MOVIE:
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
                                MediaType.MOVIE);

                    case TV_SHOW:
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
                                MediaType.TV_SHOW);

                    default:
                        return null;
                }
            } catch (TmdbException ex) {
                return null;
            }
        });
    }

    @Override
    public List<MediaDetailsItem> searchMediaByName(String query, int page) {
        if (query.isEmpty()) {
            return new ArrayList<>();
        }

        // Generate a cache key based on the query and page number
        String cacheKey = query + ":" + page;

        return searchCache.get(cacheKey, key -> {
            try {
                var media = tmdbApi.getSearch()
                        .searchMulti(query, true, "en", page)
                        .getResults();

                return media.stream()
                        .map(result -> {
                            switch (result) {
                                case MultiMovie movie -> {
                                    return new MediaDetailsItem(
                                            Media.generateId(String.valueOf(movie.getId()), MediaType.MOVIE),
                                            String.valueOf(movie.getId()),
                                            movie.getTitle(),
                                            parseMediaDate(movie.getReleaseDate()),
                                            TMDB_IMAGE_BASE_URL + movie.getPosterPath(),
                                            MediaType.MOVIE
                                    );
                                }

                                case MultiTvSeries tvShow -> {
                                    return new MediaDetailsItem(
                                            Media.generateId(String.valueOf(tvShow.getId()), MediaType.TV_SHOW),
                                            String.valueOf(tvShow.getId()),
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
        });
    }

    private LocalDate parseMediaDate(String date) {
        return date.isEmpty() ? null : LocalDate.parse(date);
    }
}

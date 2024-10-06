package pt.graca.service.external.content;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.model.tv.series.TvSeriesDb;
import info.movito.themoviedbapi.tools.TmdbException;
import pt.graca.domain.MediaType;
import pt.graca.service.results.MediaResult;

import java.util.List;
import java.util.NoSuchElementException;

public class TmdbProvider implements IContentProvider {

    private final TmdbApi tmdbApi = new TmdbApi(System.getenv("RANKFLIX_TMDB_API_KEY"));
    private final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/original";

    public MediaResult getMovieDetails(String tmdbId) {
        try {
            MovieDb movie = tmdbApi.getMovies().getDetails(Integer.parseInt(tmdbId), "en");
            return new MediaResult(
                    movie.getId() + "",
                    movie.getTitle(),
                    movie.getOverview(),
                    TMDB_IMAGE_BASE_URL + movie.getPosterPath(),
                    MediaType.MOVIE
            );
        } catch (TmdbException e) {
            throw new NoSuchElementException("Movie not found");
        }
    }

    public MediaResult getTvShowDetails(String tmdbId) {
        try {
            TvSeriesDb tvShow = tmdbApi.getTvSeries().getDetails(Integer.parseInt(tmdbId), "en");
            return new MediaResult(
                    tvShow.getId() + "",
                    tvShow.getName(),
                    tvShow.getOverview(),
                    TMDB_IMAGE_BASE_URL + tvShow.getPosterPath(),
                    MediaType.TV_SHOW
            );
        } catch (TmdbException e) {
            throw new NoSuchElementException("TV Show not found");
        }
    }

    public List<MediaResult> search(String query, int page) {
        try {
            var media = tmdbApi.getSearch().searchMulti(query, true, "en", page).getResults();
            return media.stream()
                    .map(result -> {
                        if (result instanceof MovieDb movie) {
                            return new MediaResult(
                                    movie.getId() + "",
                                    movie.getTitle(),
                                    movie.getOverview(),
                                    TMDB_IMAGE_BASE_URL + movie.getPosterPath(),
                                    MediaType.MOVIE
                            );
                        } else if (result instanceof TvSeriesDb tvShow) {
                            return new MediaResult(
                                    tvShow.getId() + "",
                                    tvShow.getName(),
                                    tvShow.getOverview(),
                                    TMDB_IMAGE_BASE_URL + tvShow.getPosterPath(),
                                    MediaType.TV_SHOW
                            );
                        } else {
                            throw new IllegalArgumentException("Unknown media type");
                        }
                    })
                    .toList();
        } catch (TmdbException e) {
            throw new NoSuchElementException("No TV Shows found");
        }
    }
}

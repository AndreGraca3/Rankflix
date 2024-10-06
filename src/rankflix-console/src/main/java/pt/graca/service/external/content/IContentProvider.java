package pt.graca.service.external.content;

import pt.graca.service.results.MediaResult;

public interface IContentProvider {
    MediaResult getMovieDetails(String tmdbId);

    MediaResult getTvShowDetails(String tmdbId);
}

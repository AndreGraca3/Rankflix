using Microsoft.AspNetCore.Mvc;
using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Media;
using Rankflix.Application.Service.External;

namespace Rankflix.Application.Http.Controllers.Media;

[ApiController]
public class MovieController(IContentProvider contentProvider) : ControllerBase
{
    [HttpGet(Uris.Movie.Base)]
    public async Task<ActionResult<PaginatedResult<MovieItem>>> GetMoviesAsync([FromQuery] string? title,
        [FromQuery] int page = 1)
    {
        return await contentProvider.SearchMovies(title, page);
    }

    [HttpGet(Uris.Movie.Trending)]
    public async Task<ActionResult<PaginatedResult<MovieItem>>> GetTrendingMoviesAsync(int page = 1)
    {
        return await contentProvider.GetTrendingMovies(page);
    }

    [HttpGet(Uris.Movie.MovieByTmdbId)]
    public async Task<ActionResult<Movie>> GetMovieByIdAsync([FromRoute] int tmdbId)
    {
        return await contentProvider.GetMovieById(tmdbId);
    }
}
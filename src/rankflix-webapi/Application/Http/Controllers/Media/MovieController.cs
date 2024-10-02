using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.RateLimiting;
using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Media;
using Rankflix.Application.Service.External.Media;

namespace Rankflix.Application.Http.Controllers.Media;

[ApiController]
[EnableRateLimiting("fixed")]
public partial class MediaController(IContentProvider contentProvider): ControllerBase
{
    [HttpGet(Uris.Media.Movies.Search)]
    public async Task<ActionResult<PaginatedResult<MovieItem>>> SearchMoviesAsync([FromQuery] string? title,
        [FromQuery] int page = 1)
    {
        return await contentProvider.SearchMovies(title, page);
    }

    [HttpGet(Uris.Media.Movies.Trending)]
    public async Task<ActionResult<PaginatedResult<MovieItem>>> GetTrendingMoviesAsync(int page = 1)
    {
        return await contentProvider.GetTrendingMovies(page);
    }

    [HttpGet(Uris.Media.Movies.MovieByTmdbId)]
    public async Task<ActionResult<Movie>> GetMovieByTmdbIdAsync([FromRoute] int tmdbId)
    {
        return await contentProvider.GetMovieByTmdbId(tmdbId);
    }
}
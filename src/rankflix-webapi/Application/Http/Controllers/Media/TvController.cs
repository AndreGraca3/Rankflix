using Microsoft.AspNetCore.Mvc;
using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Media;
using Rankflix.Application.Service.External;
using Rankflix.Application.Service.External.Media;

namespace Rankflix.Application.Http.Controllers.Media;

[ApiController]
public class TvController(IContentProvider contentProvider) : ControllerBase
{
    [HttpGet(Uris.Tv.Base)]
    public async Task<ActionResult<PaginatedResult<TvShowItem>>> GetTvShowsAsync([FromQuery] string? title, [FromQuery] int page = 1)
    {
        return await contentProvider.SearchTvShows(title, page);
    }

    [HttpGet(Uris.Tv.Trending)]
    public async Task<ActionResult<PaginatedResult<TvShowItem>>> GetTrendingTvShowsAsync(int page = 1)
    {
        return await contentProvider.GetTrendingTvShows(page);
    }

    [HttpGet(Uris.Tv.TvShowByTmdbId)]
    public async Task<ActionResult<TvShow>> GetTvShowByIdAsync([FromRoute] int tmdbId)
    {
        return await contentProvider.GetTvShowById(tmdbId);
    }
}
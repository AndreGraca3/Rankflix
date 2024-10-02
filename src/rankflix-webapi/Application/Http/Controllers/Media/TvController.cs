using Microsoft.AspNetCore.Mvc;
using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Media;
using Rankflix.Application.Service.External;
using Rankflix.Application.Service.External.Media;

namespace Rankflix.Application.Http.Controllers.Media;

public partial class MediaController
{
    [HttpGet(Uris.Media.Tv.Search)]
    public async Task<ActionResult<PaginatedResult<TvShowItem>>> SearchTvShowsAsync([FromQuery] string? title,
        [FromQuery] int page = 1)
    {
        return await contentProvider.SearchTvShows(title, page);
    }

    [HttpGet(Uris.Media.Tv.Trending)]
    public async Task<ActionResult<PaginatedResult<TvShowItem>>> GetTrendingTvShowsAsync(int page = 1)
    {
        return await contentProvider.GetTrendingTvShows(page);
    }

    [HttpGet(Uris.Media.Tv.TvByTmdbId)]
    public async Task<ActionResult<TvShow>> GetTvShowByTmdbIdAsync([FromRoute] int tmdbId)
    {
        return await contentProvider.GetTvShowByTmdbId(tmdbId);
    }
}
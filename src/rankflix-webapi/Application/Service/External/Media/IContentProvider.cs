using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Media;

namespace Rankflix.Application.Service.External.Media;

public interface IContentProvider
{
    public Task<PaginatedResult<MovieItem>> SearchMovies(string? title, int page);
    
    public Task<PaginatedResult<TvShowItem>> SearchTvShows(string? title, int page);
    
    public Task<PaginatedResult<MovieItem>> GetTrendingMovies(int page);
    
    public Task<PaginatedResult<TvShowItem>> GetTrendingTvShows(int page);

    public Task<Movie> GetMovieByTmdbId(int id);
    
    public Task<TvShow> GetTvShowByTmdbId(int id);
}
using Microsoft.IdentityModel.Tokens;
using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Media;
using TMDbLib.Client;
using TMDbLib.Objects.Trending;

namespace Rankflix.Application.Service.External.Media;

public class TmdbContentProvider(TMDbClient client) : IContentProvider, IHostedService
{
    private const string BaseImageUrl = "https://image.tmdb.org/t/p/original";
    private const int TmdbItemsPerPage = 20;

    private readonly Dictionary<int, string> _movieGenres = new();
    private readonly Dictionary<int, string> _tvShowGenres = new();

    public async Task StartAsync(CancellationToken cancellationToken)
    {
        var movieGenresTask = client.GetMovieGenresAsync(cancellationToken);
        var tvShowGenresTask = client.GetTvGenresAsync(cancellationToken);
        await Task.WhenAll(movieGenresTask, tvShowGenresTask);

        foreach (var genre in movieGenresTask.Result)
        {
            _movieGenres.Add(genre.Id, genre.Name);
        }

        foreach (var genre in tvShowGenresTask.Result)
        {
            _tvShowGenres.Add(genre.Id, genre.Name);
        }
    }

    public Task StopAsync(CancellationToken cancellationToken)
    {
        return Task.CompletedTask;
    }

    public async Task<PaginatedResult<MovieItem>> SearchMovies(string? title, int page)
    {
        if (title.IsNullOrEmpty())
            return new PaginatedResult<MovieItem>(new List<MovieItem>(), 0, 0, TmdbItemsPerPage);

        var paginatedMovies = await client.SearchMovieAsync(title, page);

        return new PaginatedResult<MovieItem>(
            paginatedMovies.Results.Select(m => new MovieItem
            {
                Id = m.Id,
                Title = m.Title,
                Type = MediaType.Movie,
                Overview = m.Overview,
                ReleaseDate = m.ReleaseDate,
                PosterUrl = $"{BaseImageUrl}{m.PosterPath}",
                Genres = m.GenreIds.Select(gId => _movieGenres[gId])
                    .ToArray(),
                ExternalRating = m.VoteAverage
            }).ToList(),
            paginatedMovies.TotalResults,
            paginatedMovies.Page * paginatedMovies.Results.Count,
            TmdbItemsPerPage
        );
    }

    public async Task<PaginatedResult<TvShowItem>> SearchTvShows(string? title, int page)
    {
        if (title.IsNullOrEmpty())
            return new PaginatedResult<TvShowItem>(new List<TvShowItem>(), 0, 0, TmdbItemsPerPage);

        var paginatedTvShows = await client.SearchTvShowAsync(title, page);

        return new PaginatedResult<TvShowItem>(
            paginatedTvShows.Results.Select(t => new TvShowItem
            {
                Id = t.Id,
                Title = t.Name,
                Type = MediaType.Tv,
                Overview = t.Overview,
                PosterUrl = $"{BaseImageUrl}{t.PosterPath}",
                Genres = t.GenreIds.Select(gId => _tvShowGenres[gId])
                    .ToArray(),
                ExternalRating = t.VoteAverage,
                FirstAirDate = t.FirstAirDate
            }).ToList(),
            paginatedTvShows.TotalResults,
            paginatedTvShows.Page * paginatedTvShows.Results.Count,
            TmdbItemsPerPage
        );
    }

    public async Task<PaginatedResult<MovieItem>> GetTrendingMovies(int page)
    {
        var paginatedTrendingMovies = await client.GetTrendingMoviesAsync(TimeWindow.Week, page);

        return new PaginatedResult<MovieItem>(
            paginatedTrendingMovies.Results.Select(m => new MovieItem
            {
                Id = m.Id,
                Title = m.Title,
                Type = MediaType.Movie,
                Overview = m.Overview,
                ReleaseDate = m.ReleaseDate,
                PosterUrl = $"{BaseImageUrl}{m.PosterPath}",
                Genres = m.GenreIds.Select(gId => _movieGenres[gId])
                    .ToArray(),
                ExternalRating = m.VoteAverage
            }).ToList(),
            paginatedTrendingMovies.TotalResults,
            paginatedTrendingMovies.Page * paginatedTrendingMovies.Results.Count,
            TmdbItemsPerPage
        );
    }

    public async Task<PaginatedResult<TvShowItem>> GetTrendingTvShows(int page)
    {
        var paginatedTrendingTvShows = await client.GetTrendingTvAsync(TimeWindow.Week, page);

        return new PaginatedResult<TvShowItem>(
            paginatedTrendingTvShows.Results.Select(t => new TvShowItem
            {
                Id = t.Id,
                Title = t.Name,
                Type = MediaType.Tv,
                Overview = t.Overview,
                PosterUrl = $"{BaseImageUrl}{t.PosterPath}",
                Genres = t.GenreIds.Select(gId => _tvShowGenres[gId])
                    .ToArray(),
                ExternalRating = t.VoteAverage,
                FirstAirDate = t.FirstAirDate
            }).ToList(),
            paginatedTrendingTvShows.TotalResults,
            paginatedTrendingTvShows.Page * paginatedTrendingTvShows.Results.Count,
            TmdbItemsPerPage
        );
    }

    public async Task<Movie> GetMovieByTmdbId(int id)
    {
        var movie = await client.GetMovieAsync(id);

        return new Movie
        {
            Id = movie.Id,
            Title = movie.Title,
            Type = MediaType.Movie,
            Overview = movie.Overview,
            PosterUrl = $"{BaseImageUrl}{movie.PosterPath}",
            Genres = movie.Genres.Select(g => g.Name).ToArray(),
            ExternalRating = movie.VoteAverage,
            ReleaseDate = movie.ReleaseDate,
            Runtime = movie.Runtime ?? 0,
            Director = movie.Credits.Crew.FirstOrDefault(c => c.Job == "Director")?.Name ?? ""
        };
    }

    public async Task<TvShow> GetTvShowByTmdbId(int id)
    {
        var tvShow = await client.GetTvShowAsync(id);

        return new TvShow
        {
            Id = tvShow.Id,
            Title = tvShow.Name,
            Type = MediaType.Tv,
            Overview = tvShow.Overview,
            PosterUrl = $"{BaseImageUrl}{tvShow.PosterPath}",
            Genres = tvShow.Genres.Select(g => g.Name)
                .ToArray(),
            ExternalRating = tvShow.VoteAverage,
            FirstAirDate = tvShow.FirstAirDate,
            LastAirDate = tvShow.LastAirDate,
            Seasons = tvShow.Seasons.Count,
            Creators = tvShow.CreatedBy.Select(c => c.Name)
                .ToArray()
        };
    }
}
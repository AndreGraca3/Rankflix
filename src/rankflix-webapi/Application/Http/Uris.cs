using System.Text.RegularExpressions;

namespace Rankflix.Application.Http;

public static class Uris
{
    public const string ApiBase = "/api";

    public static class Auth
    {
        public const string Base = $"{ApiBase}/auth";
        public const string DiscordAuth = $"{Base}/discord-sign-in";
        public const string Logout = $"{Base}/sign-out";
        public const string RefreshToken = $"{Base}/refresh-token";
    }

    public static class User
    {
        public const string Base = $"{ApiBase}/users";
        public const string UserById = $"{Base}/{{userId}}";
        public const string Me = $"{Base}/me";
    }

    public static class Movie
    {
        public const string Base = $"{ApiBase}/movies";
        public const string Search = $"{Base}/search";
        public const string Trending = $"{Base}/trending";
        public const string MovieByTmdbId = $"{Base}/{{tmdbId}}";
    }

    public static class Tv
    {
        public const string Base = $"{ApiBase}/tv";
        public const string Search = $"{Base}/search";
        public const string Trending = $"{Base}/trending";
        public const string TvShowByTmdbId = $"{Base}/{{tmdbId}}";
    }

    public static class Review
    {
        public const string Base = $"{ApiBase}/reviews";
        public const string ReviewById = $"{Base}/{{reviewId}}";

        public static string BuildReviewByIdUri(Guid id) => ReviewById.ExpandUri(id);
    }

    public static class Group
    {
        public const string Base = $"{ApiBase}/groups";
        public const string GroupById = $"{Base}/{{groupId}}";
        public const string Watchlist = GroupById + "/watchlist";

        public static string BuildGroupByIdUri(Guid id) => GroupById.ExpandUri(id);
    }

    private static string ExpandUri(this string input, params object[] args)
    {
        var result = input;
        var argIndex = 0;
        result = Regex.Replace(
            result,
            @"\{(.*?)\}",
            _ =>
                args[argIndex++].ToString()
                ?? throw new ArgumentException(
                    "Not enough arguments provided to replace all placeholders."
                )
        );
        return result;
    }
}
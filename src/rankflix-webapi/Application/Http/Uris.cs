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
        public const string RefreshToken = $"{Base}/refresh";
    }

    public static class User
    {
        public const string Base = $"{ApiBase}/users";
        public const string UserById = $"{Base}/{{userId}}";
        public const string Me = $"{Base}/me";
    }

    public static class Media
    {
        public const string MediaBase = $"{ApiBase}/media";

        public class Movies
        {
            public const string Base = MediaBase + "/movies";
            public const string Search = Base + "/search";
            public const string Trending = Base + "/trending";
            public const string MovieByTmdbId = Base + "/{{tmdbId}}";

            public static string BuildMovieByTmdbIdUri(int tmdbId) => MovieByTmdbId.ExpandUri(tmdbId);
        }
        
        public class Tv
        {
            public const string Base = MediaBase + "/tv";
            public const string Search = Base + "/search";
            public const string Trending = Base + "/trending";
            public const string TvByTmdbId = Base + "/{{tmdbId}}";

            public static string BuildTvByTmdbIdUri(int tmdbId) => TvByTmdbId.ExpandUri(tmdbId);
        }
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
namespace Rankflix.Application.Domain.Media;

public class Media : MediaItem
{
    public required string Overview { get; set; }

    public required string PosterUrl { get; set; }

    public required string[] Genres { get; set; }

    public required double ExternalRating { get; set; }
}

public class MediaItem
{
    public int Id { get; set; }

    public required string Title { get; set; }

    public required MediaType Type { get; set; }
}

public enum MediaType
{
    Movie,
    Tv
}
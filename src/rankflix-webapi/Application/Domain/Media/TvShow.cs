namespace Rankflix.Application.Domain.Media;

public class TvShow : Media
{
    public required int Seasons { get; set; }

    public required string[] Creators { get; set; }

    public required DateTime? FirstAirDate { get; set; }

    public required DateTime? LastAirDate { get; set; }
}

public class TvShowItem : Media
{
    public required DateTime? FirstAirDate { get; set; }
}
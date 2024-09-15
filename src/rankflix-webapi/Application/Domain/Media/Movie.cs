namespace Rankflix.Application.Domain.Media;

public class Movie: Media
{
    public DateTime? ReleaseDate { get; set; }

    public required int Runtime { get; set; }
    
    public required string Director { get; set; }
}

public class MovieItem : Media
{
    public DateTime? ReleaseDate { get; set; }
}
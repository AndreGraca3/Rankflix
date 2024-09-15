namespace Rankflix.Application.Domain;

public class Review
{
    public ReviewId Id { get; set; }

    public int Rating { get; set; }

    public string Comment { get; set; }

    public DateTime CreatedAt { get; set; }

    public Guid UserId { get; set; }

    public int MediaId { get; set; }
}

public class ReviewId
{
    public Guid Value { get; set; }
}
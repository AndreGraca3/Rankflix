using Rankflix.Application.Domain.Account;

namespace Rankflix.Application.Domain;

public class Review
{
    public required ReviewId Id { get; set; }

    public required float Rating { get; set; }

    public required string Comment { get; set; }

    public required DateTime CreatedAt { get; set; }

    public required UserId UserId { get; set; }

    public required int MediaId { get; set; }

    public bool IsOwner(UserId userId) => UserId.Value == userId.Value;
    
    public bool IsTooOld() => DateTime.UtcNow - CreatedAt > TimeSpan.FromMinutes(5);
}

public record ReviewId(int Value);
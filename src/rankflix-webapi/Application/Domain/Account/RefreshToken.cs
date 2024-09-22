namespace Rankflix.Application.Domain.Account;

public class RefreshToken
{
    public Guid Value { get; init; }

    public required DateTime CreatedAt { get; init; }

    public required DateTime ExpiresAt { get; init; }
}

public class RefreshTokenItem
{
    public Guid Value { get; init; }

    public DateTime CreatedAt { get; init; }

    public required UserId UserId { get; init; }
    
    public RefreshToken ToRefreshToken(DateTime expiresAt)
    {
        return new RefreshToken
        {
            Value = Value,
            CreatedAt = CreatedAt,
            ExpiresAt = expiresAt
        };
    }
}
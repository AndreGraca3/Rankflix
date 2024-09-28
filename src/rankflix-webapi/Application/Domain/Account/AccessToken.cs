namespace Rankflix.Application.Domain.Account;

public class AccessToken
{
    public required string Value { get; init; }

    public DateTime ExpiresAt { get; init; }
}
namespace Rankflix.Application.Domain.Account;

public class AccessToken
{
    public required string Value { get; set; }

    public DateTime ExpiresAt { get; set; }
}
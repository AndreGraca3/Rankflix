namespace Rankflix.Application.Domain.Account;

public class User
{
    public required UserId Id { get; set; }

    public required string Username { get; set; }

    public required string Email { get; set; }

    public string? AvatarUrl { get; set; }

    public required DateTime CreatedAt { get; set; }
}

public record UserItem(int Id, string Username, string? AvatarUrl);

public record UserId(int Value);
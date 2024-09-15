namespace Rankflix.Application.Domain;

public class User
{
    public UserId Id { get; set; }

    public string Username { get; set; }

    public string Email { get; set; }

    public string? AvatarUrl { get; set; }

    public DateTime CreatedAt { get; set; }
}

public class UserId
{
    public int Value { get; set; }
}
namespace Rankflix.Application.Http.Models.Account.User;

public class UserOutputModel
{
    public int Id { get; set; }

    public required string Name { get; set; }

    public string? AvatarUrl { get; set; }

    public DateTime CreatedAt { get; set; }
}

public static class UserOutputModelExtensions
{
    public static UserOutputModel ToOutputModel(this Domain.User user)
    {
        return new UserOutputModel
        {
            Id = user.Id.Value,
            Name = user.Username,
            AvatarUrl = user.AvatarUrl,
            CreatedAt = user.CreatedAt
        };
    }
}
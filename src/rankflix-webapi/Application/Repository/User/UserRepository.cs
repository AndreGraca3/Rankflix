using Microsoft.EntityFrameworkCore;
using Rankflix.Application.Domain;
using Rankflix.Infrastructure.Data;
using Rankflix.Infrastructure.Data.Tables;

namespace Rankflix.Application.Repository.User;

public class UserRepository(RankflixDataContext dataContext) : IUserRepository
{
    private const int PageSize = 20;

    public async Task<PaginatedResult<Domain.User>> GetUsersAsync(int page)
    {
        var skip = (page - 1) * PageSize;
        var query = dataContext.User
            .Skip(skip)
            .Take(PageSize)
            .Select(u => new Domain.User
            {
                Id = new UserId { Value = u.Id },
                Username = u.Username,
                Email = u.Email,
                AvatarUrl = u.AvatarUrl
            });

        var paginatedUsers = await query.ToListAsync();
        return new PaginatedResult<Domain.User>(paginatedUsers, query.Count(), skip, PageSize);
    }

    public async Task<Domain.User?> GetUserByIdAsync(UserId userId)
    {
        return await dataContext.User
            .Where(u => u.Id == userId.Value)
            .Select(u => new Domain.User
            {
                Id = new UserId { Value = u.Id },
                Username = u.Username,
                Email = u.Email,
                AvatarUrl = u.AvatarUrl
            })
            .FirstOrDefaultAsync();
    }

    public async Task<UserId> AddUserAsync(string name, string email, string? avatarUrl)
    {
        var user = new UserEntity
        {
            Username = name,
            Email = email,
            AvatarUrl = avatarUrl,
            CreatedAt = DateTime.UtcNow,
        };

        dataContext.User.Add(user);
        await dataContext.SaveChangesAsync();

        return new UserId { Value = user.Id };
    }

    public async Task<Domain.User?> UpdateUserAsync(UserId userId, string name, string email, string? avatarUrl)
    {
        var user = await dataContext.User
            .Where(u => u.Id == userId.Value)
            .FirstOrDefaultAsync();

        if (user == null) return null;

        user.Username = name;
        user.Email = email;
        user.AvatarUrl = avatarUrl;

        await dataContext.SaveChangesAsync();

        return new Domain.User
        {
            Id = new UserId { Value = user.Id },
            Username = user.Username,
            Email = user.Email,
            AvatarUrl = user.AvatarUrl
        };
    }

    public async Task DeleteUserAsync(UserId userId)
    {
        var user = await dataContext.User
            .Where(u => u.Id == userId.Value)
            .FirstOrDefaultAsync();

        if (user == null) return;

        dataContext.User.Remove(user);
        await dataContext.SaveChangesAsync();
    }
}
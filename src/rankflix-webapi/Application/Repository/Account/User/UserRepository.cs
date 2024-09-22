using Microsoft.EntityFrameworkCore;
using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;
using Rankflix.Infrastructure.Data;
using Rankflix.Infrastructure.Data.Tables;

namespace Rankflix.Application.Repository.Account.User;

public class UserRepository(RankflixDataContext dataContext) : IUserRepository
{
    private const int PageSize = 20;

    public async Task<PaginatedResult<Domain.Account.User>> GetUsersAsync(int page, string? username)
    {
        var skip = (page - 1) * PageSize;
        var query = dataContext.User
            .Skip(skip)
            .Take(PageSize)
            .Select(u => new Domain.Account.User
            {
                Id = new UserId(u.Id),
                Username = u.Username,
                Email = u.Email,
                AvatarUrl = u.AvatarUrl,
                CreatedAt = u.CreatedAt
            });

        var paginatedUsers = await query.ToListAsync();
        return new PaginatedResult<Domain.Account.User>(paginatedUsers, query.Count(), skip, PageSize);
    }

    public async Task<Domain.Account.User?> GetUserByIdAsync(UserId userId)
    {
        return await dataContext.User
            .Where(u => u.Id == userId.Value)
            .Select(u => new Domain.Account.User
            {
                Id = new UserId(u.Id),
                Username = u.Username,
                Email = u.Email,
                AvatarUrl = u.AvatarUrl,
                CreatedAt = u.CreatedAt
            })
            .FirstOrDefaultAsync();
    }

    public async Task<Domain.Account.User?> GetUserByEmailAsync(string email)
    {
        return await dataContext.User
            .Where(u => u.Email == email)
            .Select(u => new Domain.Account.User
            {
                Id = new UserId(u.Id),
                Username = u.Username,
                Email = u.Email,
                AvatarUrl = u.AvatarUrl,
                CreatedAt = u.CreatedAt
            })
            .FirstOrDefaultAsync();
    }

    public async Task<Domain.Account.User?> GetUserByUsernameAsync(string username)
    {
        return await dataContext.User
            .Where(u => u.Username == username)
            .Select(u => new Domain.Account.User
            {
                Id = new UserId(u.Id),
                Username = u.Username,
                Email = u.Email,
                AvatarUrl = u.AvatarUrl,
                CreatedAt = u.CreatedAt
            })
            .FirstOrDefaultAsync();
    }

    public async Task<Domain.Account.User?> GetUserByEmailOrUsernameAsync(string email, string username)
    {
        return await dataContext.User
            .Where(u => u.Email == email || u.Username == username)
            .Select(u => new Domain.Account.User
            {
                Id = new UserId(u.Id),
                Username = u.Username,
                Email = u.Email,
                AvatarUrl = u.AvatarUrl,
                CreatedAt = u.CreatedAt
            })
            .FirstOrDefaultAsync();
    }

    public async Task<(bool EmailExists, bool UsernameExists)> CheckUserUniquenessAsync(string email, string username)
    {
        var user = await dataContext.User
            .Where(u => u.Email == email || u.Username == username)
            .Select(u => new { u.Email, u.Username })
            .FirstOrDefaultAsync();

        return (user?.Email == email, user?.Username == username);
    }

    public async Task<UserId> AddUserAsync(string email, string username, string? avatarUrl)
    {
        var user = new UserEntity
        {
            Username = username,
            Email = email,
            AvatarUrl = avatarUrl,
            CreatedAt = DateTime.UtcNow,
        };

        dataContext.User.Add(user);
        await dataContext.SaveChangesAsync();

        return new UserId(user.Id);
    }

    public async Task<Domain.Account.User?> UpdateUserAsync(UserId userId, string name, string email, string? avatarUrl)
    {
        var user = await dataContext.User
            .Where(u => u.Id == userId.Value)
            .FirstOrDefaultAsync();

        if (user == null) return null;

        user.Username = name;
        user.Email = email;
        user.AvatarUrl = avatarUrl;

        await dataContext.SaveChangesAsync();

        return new Domain.Account.User
        {
            Id = new UserId(user.Id),
            Username = user.Username,
            Email = user.Email,
            AvatarUrl = user.AvatarUrl,
            CreatedAt = user.CreatedAt
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
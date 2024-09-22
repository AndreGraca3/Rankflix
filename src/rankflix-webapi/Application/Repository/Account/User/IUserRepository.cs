using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;

namespace Rankflix.Application.Repository.Account.User;

public interface IUserRepository
{
    public Task<PaginatedResult<Domain.Account.User>> GetUsersAsync(int page, string? username);

    public Task<Domain.Account.User?> GetUserByIdAsync(UserId userId);

    public Task<Domain.Account.User?> GetUserByEmailAsync(string email);

    public Task<Domain.Account.User?> GetUserByUsernameAsync(string username);

    public Task<Domain.Account.User?> GetUserByEmailOrUsernameAsync(string email, string username);

    public Task<UserId> AddUserAsync(string email, string username, string? avatarUrl);

    public Task<Domain.Account.User?> UpdateUserAsync(UserId userId, string name, string email, string? avatarUrl);

    public Task DeleteUserAsync(UserId userId);
}
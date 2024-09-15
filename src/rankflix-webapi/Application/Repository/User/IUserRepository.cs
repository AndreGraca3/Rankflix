using Rankflix.Application.Domain;

namespace Rankflix.Application.Repository.User;

public interface IUserRepository
{
    public Task<PaginatedResult<Domain.User>> GetUsersAsync(int page);
    
    public Task<Domain.User?> GetUserByIdAsync(UserId userId);
    
    public Task<UserId> AddUserAsync(string name, string email, string? avatarUrl);
    
    public Task<Domain.User?> UpdateUserAsync(UserId userId, string name, string email, string? avatarUrl);
    
    public Task DeleteUserAsync(UserId userId);
}
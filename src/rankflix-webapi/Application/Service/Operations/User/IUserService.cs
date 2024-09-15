using Rankflix.Application.Domain;

namespace Rankflix.Application.Service.Operations.User;

public interface IUserService
{
    Task<PaginatedResult<Domain.User>> GetUsers(string? username);
    
    Task<Domain.User> AddUser(string username, string email, string? avatarUrl);
}
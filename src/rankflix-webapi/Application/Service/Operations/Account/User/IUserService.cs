using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;

namespace Rankflix.Application.Service.Operations.Account.User;

public interface IUserService
{
    Task<PaginatedResult<Domain.Account.User>> GetUsersAsync(int page, string? username);

    Task<Domain.Account.User?> GetUserByIdAsync(int id);

    Task<Domain.Account.User?> GetUserByEmailAsync(string username);
}
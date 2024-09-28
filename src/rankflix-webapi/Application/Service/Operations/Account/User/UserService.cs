using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;
using Rankflix.Application.Repository.Account.User;
using Rankflix.Application.Service.Transaction;

namespace Rankflix.Application.Service.Operations.Account.User;

public class UserService(IUserRepository userRepository, ITransactionManager transactionManager) : IUserService
{
    public async Task<PaginatedResult<Domain.Account.User>> GetUsersAsync(int skip, int take, string? username)
    {
        return await transactionManager.ExecuteAsync(async () =>
            await userRepository.GetUsersAsync(skip, take, username));
    }

    public async Task<Domain.Account.User?> GetUserByIdAsync(int id)
    {
        return await transactionManager.ExecuteAsync(async () =>
            await userRepository.GetUserByIdAsync(new UserId(id)));
    }

    public async Task<Domain.Account.User?> GetUserByEmailAsync(string username)
    {
        return await transactionManager.ExecuteAsync(async () =>
            await userRepository.GetUserByEmailAsync(username));
    }
}
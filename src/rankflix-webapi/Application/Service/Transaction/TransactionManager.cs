using Rankflix.Infrastructure;
using Rankflix.Infrastructure.Data;

namespace Rankflix.Application.Service.Transaction;

public class TransactionManager(RankflixDataContext dataContext) : ITransactionManager
{
    public async Task<T> ExecuteAsync<T>(Func<Task<T>> action)
    {
        await using var transaction = await dataContext.Database.BeginTransactionAsync();
        try
        {
            var result = await action();
            await transaction.CommitAsync();
            return result;
        }
        catch (Exception ex)
        {
            await transaction.RollbackAsync();
            throw;
        }
    }
}
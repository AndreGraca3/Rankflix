namespace Rankflix.Application.Service.Transaction;

public partial interface ITransactionManager
{
    Task<T> ExecuteAsync<T>(Func<Task<T>> action);
}
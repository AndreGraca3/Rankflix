namespace Rankflix.Application.Service.Transaction;

public interface ITransactionManager
{
    Task<T> ExecuteAsync<T>(Func<Task<T>> action);
}
package pt.graca.api.repo.transaction;

public interface ITransactionManager {
    <T> T run(FunctionWithException<ITransaction, T> block) throws Exception;

    void run(FunctionThatReturnsVoid<ITransaction> block) throws Exception;
}


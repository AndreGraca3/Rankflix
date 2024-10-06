package pt.graca.repo.transaction;

public interface ITransactionManager {
    <T> T run(FunctionWithException<TransactionCtx, T> block) throws Exception;

    void run(FunctionThatReturnsVoid<TransactionCtx> block) throws Exception;
}


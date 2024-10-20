package pt.graca.api.repo.transaction;

import pt.graca.api.service.exceptions.RankflixException;

public interface ITransactionManager {
    <T> T run(FunctionWithException<ITransaction, T> block) throws RankflixException;

    void run(FunctionThatReturnsVoid<ITransaction> block) throws RankflixException;
}


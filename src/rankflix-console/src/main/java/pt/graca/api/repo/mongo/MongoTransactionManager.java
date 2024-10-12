package pt.graca.api.repo.mongo;

import pt.graca.api.repo.transaction.FunctionThatReturnsVoid;
import pt.graca.api.repo.transaction.FunctionWithException;
import pt.graca.api.repo.transaction.ITransactionManager;
import pt.graca.api.repo.transaction.ITransaction;

public class MongoTransactionManager implements ITransactionManager {

    public MongoTransactionManager(MongoRepositoryTransaction transaction) {
        this.transaction = transaction;
    }

    private final MongoRepositoryTransaction transaction;

    @Override
    public <T> T run(FunctionWithException<ITransaction, T> block) throws Exception {
        transaction.begin();
        try {
            T result = block.apply(transaction);
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void run(FunctionThatReturnsVoid<ITransaction> block) throws Exception {
        transaction.begin();
        try {
            block.apply(transaction);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}

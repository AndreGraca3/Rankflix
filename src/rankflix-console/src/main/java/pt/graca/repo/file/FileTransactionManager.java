package pt.graca.repo.file;

import pt.graca.repo.transaction.FunctionThatReturnsVoid;
import pt.graca.repo.transaction.FunctionWithException;
import pt.graca.repo.transaction.ITransactionManager;
import pt.graca.repo.transaction.TransactionCtx;

public class FileTransactionManager implements ITransactionManager {

    public FileTransactionManager(FileRepositoryTransaction transaction) {
        this.transaction = transaction;
    }

    private final FileRepositoryTransaction transaction;

    @Override
    public <T> T run(FunctionWithException<TransactionCtx, T> block) throws Exception {
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
    public void run(FunctionThatReturnsVoid<TransactionCtx> block) throws Exception {
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

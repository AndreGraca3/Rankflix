package pt.graca.api.repo.file;

import com.google.gson.Gson;
import pt.graca.api.repo.transaction.FunctionThatReturnsVoid;
import pt.graca.api.repo.transaction.FunctionWithException;
import pt.graca.api.repo.transaction.ITransactionManager;
import pt.graca.api.repo.transaction.ITransaction;

public class FileTransactionManager implements ITransactionManager {

    public FileTransactionManager(Gson gson, String folderName, String listName) {
        this.gson = gson;
        this.folderName = folderName;
        this.listName = listName;
    }

    private final Gson gson;
    private final String folderName;
    private final String listName;

    @Override
    public <T> T run(FunctionWithException<ITransaction, T> block) throws Exception {
        var transaction = new FileTransaction(gson, folderName, listName);

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
        var transaction = new FileTransaction(gson, folderName, listName);

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

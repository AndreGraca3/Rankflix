package pt.graca.api.repo.file;

import com.google.gson.Gson;
import pt.graca.api.repo.transaction.FunctionThatReturnsVoid;
import pt.graca.api.repo.transaction.FunctionWithException;
import pt.graca.api.repo.transaction.ITransactionManager;
import pt.graca.api.repo.transaction.ITransaction;
import pt.graca.api.service.exceptions.RankflixException;

import java.io.IOException;

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
    public <T> T run(FunctionWithException<ITransaction, T> block) throws RankflixException {
        var transaction = new FileTransaction(gson, folderName, listName);

        transaction.begin();
        try {
            T result = block.apply(transaction);
            transaction.commit();
            return result;
        } catch (RankflixException e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void run(FunctionThatReturnsVoid<ITransaction> block) throws RankflixException {
        FileTransaction transaction = new FileTransaction(gson, folderName, listName);

        transaction.begin();
        try {
            block.apply(transaction);
            transaction.commit();
        } catch (RankflixException e) {
            transaction.rollback();
            throw e;
        }
    }
}

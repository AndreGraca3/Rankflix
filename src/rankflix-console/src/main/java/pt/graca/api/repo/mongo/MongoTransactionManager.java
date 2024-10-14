package pt.graca.api.repo.mongo;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import pt.graca.api.repo.transaction.FunctionThatReturnsVoid;
import pt.graca.api.repo.transaction.FunctionWithException;
import pt.graca.api.repo.transaction.ITransaction;
import pt.graca.api.repo.transaction.ITransactionManager;

public class MongoTransactionManager implements ITransactionManager {

    public MongoTransactionManager(MongoClient mongoClient, String listName) {
        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase("rankflix-db");
        this.listName = listName;
    }

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final String listName;

    @Override
    public <T> T run(FunctionWithException<ITransaction, T> block) throws Exception {
        var transaction = createTransaction();

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
        var transaction = createTransaction();

        transaction.begin();
        try {
            block.apply(transaction);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    private ITransaction createTransaction() {
        ClientSession session = mongoClient.startSession();
        return new MongoTransaction(database, session, listName);
    }
}

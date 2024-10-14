package pt.graca.api.repo.mongo;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import pt.graca.api.repo.IRepository;
import pt.graca.api.repo.transaction.ITransaction;

public class MongoTransaction implements ITransaction {

    public MongoTransaction(MongoDatabase database, ClientSession session, String listName) {
        if (listName.isBlank()) {
            throw new IllegalArgumentException("List name cannot be blank");
        }

        this.database = database;
        this.session = session;
        this.listName = listName;
    }

    private final MongoDatabase database;
    private final ClientSession session;
    private final String listName;

    @Override
    public IRepository getRepository() {
        return new MongoRepository(database, session, listName);
    }

    @Override
    public void begin() {
        session.startTransaction();
    }

    @Override
    public void commit() {
        session.commitTransaction();
    }

    @Override
    public void rollback() {
        session.abortTransaction();
    }

    @Override
    public void close() {
        session.close();
    }
}

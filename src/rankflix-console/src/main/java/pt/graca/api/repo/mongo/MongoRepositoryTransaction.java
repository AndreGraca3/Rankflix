package pt.graca.api.repo.mongo;

import pt.graca.api.repo.IRepository;
import pt.graca.api.repo.transaction.ITransaction;

public record MongoRepositoryTransaction(MongoRepository repository) implements ITransaction {

    @Override
    public IRepository getRepository() {
        return repository;
    }

    @Override
    public void begin() {
        // Nothing to do here
    }

    @Override
    public void commit() {
        // Nothing to do here
    }

    @Override
    public void rollback() {
        // Nothing to do here
    }
}

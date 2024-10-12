package pt.graca.api.repo.file;

import pt.graca.api.repo.IRepository;
import pt.graca.api.repo.transaction.ITransaction;

import java.io.IOException;

public record FileRepositoryTransaction(FileRepository repository) implements ITransaction {

    @Override
    public IRepository getRepository() {
        return repository;
    }

    @Override
    public void begin() {
        // Nothing to do here
    }

    @Override
    public void commit() throws IOException {
        repository.saveData();
    }

    @Override
    public void rollback() {
        // Nothing to do here
    }
}

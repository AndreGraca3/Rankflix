package pt.graca.repo.file;

import pt.graca.repo.IRepository;
import pt.graca.repo.transaction.TransactionCtx;

import java.io.IOException;

public record FileRepositoryTransaction(FileRepository repository) implements TransactionCtx {

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

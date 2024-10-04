package pt.graca.repo.transaction;

import pt.graca.repo.IRepository;

import java.io.IOException;

public interface TransactionCtx {

    public IRepository getRepository();

    public abstract void begin();

    public abstract void commit() throws IOException;

    public abstract void rollback();
}

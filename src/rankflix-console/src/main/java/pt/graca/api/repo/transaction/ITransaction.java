package pt.graca.api.repo.transaction;

import pt.graca.api.repo.IRepository;

import java.io.IOException;

public interface ITransaction {

    public IRepository getRepository();

    public abstract void begin();

    public abstract void commit() throws IOException;

    public abstract void rollback();

    public abstract void close();
}

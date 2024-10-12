package pt.graca.api.repo.factory;

import pt.graca.api.repo.IRepository;
import pt.graca.api.repo.transaction.ITransaction;
import pt.graca.api.repo.transaction.ITransactionManager;

public abstract class RepositoryFactory {
    public abstract IRepository createRepository();

    public abstract ITransaction createTransaction();

    public abstract ITransactionManager createTransactionManager();
}

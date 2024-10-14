package pt.graca.api.repo.factory;

import pt.graca.api.repo.transaction.ITransactionManager;

public abstract class TransactionManagerFactory {
    public abstract ITransactionManager createTransactionManager();
}

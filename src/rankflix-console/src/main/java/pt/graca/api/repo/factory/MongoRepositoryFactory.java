package pt.graca.api.repo.factory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import pt.graca.api.repo.IRepository;
import pt.graca.api.repo.mongo.MongoRepository;
import pt.graca.api.repo.mongo.MongoRepositoryTransaction;
import pt.graca.api.repo.mongo.MongoTransactionManager;
import pt.graca.api.repo.transaction.ITransaction;
import pt.graca.api.repo.transaction.ITransactionManager;

import java.util.Scanner;

public class MongoRepositoryFactory extends RepositoryFactory {

    public MongoRepositoryFactory(String mongoUrl, Scanner scanner) {
        MongoClient mongoClient = MongoClients.create(mongoUrl);
            this.database = mongoClient.getDatabase("rankflix-db");

        this.scanner = scanner;
    }

    private final Scanner scanner;
    private final MongoDatabase database;

    @Override
    public IRepository createRepository() {
        System.out.println("Searching for lists...");
        var lists = database.getCollection("lists");
        if(lists.countDocuments() != 0) {
            System.out.println("Lists available:");
            lists.find().forEach(document -> System.out.println("• " + document.get("name")));
        } else {
            System.out.println("No lists available.");
        }
        System.out.println();

        System.out.print("Create/Select list: ");
        String listName = scanner.nextLine();
        return new MongoRepository(database, listName);
    }

    @Override
    public ITransaction createTransaction() {
        var repository = createRepository();

        if (!(repository instanceof MongoRepository)) {
            throw new IllegalArgumentException("Repository is not a MongoRepository");
        }

        return new MongoRepositoryTransaction((MongoRepository) repository);
    }

    @Override
    public ITransactionManager createTransactionManager() {
        var transaction = createTransaction();

        if (!(transaction instanceof MongoRepositoryTransaction)) {
            throw new IllegalArgumentException("Transaction is not a MongoRepositoryTransaction");
        }

        return new MongoTransactionManager((MongoRepositoryTransaction) transaction);
    }
}

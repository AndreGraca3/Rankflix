package pt.graca.api.repo.factory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import pt.graca.api.repo.mongo.MongoTransactionManager;
import pt.graca.api.repo.transaction.ITransactionManager;

import java.util.Scanner;

public class MongoTransactionManagerFactory extends TransactionManagerFactory {

    public MongoTransactionManagerFactory(String mongoUrl, Scanner scanner) {
        mongoClient = MongoClients.create(mongoUrl);
        database = mongoClient.getDatabase("rankflix-db");

        this.scanner = scanner;
    }

    private final Scanner scanner;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    @Override
    public ITransactionManager createTransactionManager() {
        System.out.println("Searching for lists...");
        var lists = database.getCollection("lists");
        if (lists.countDocuments() != 0) {
            System.out.println("Lists available:");
            lists.find().forEach(document -> System.out.println("• " + document.get("name")));
        } else {
            System.out.println("No lists available.");
        }
        System.out.println();

        System.out.print("Create/Select list: ");
        String listName = scanner.nextLine();

        return new MongoTransactionManager(mongoClient, listName);
    }
}

package pt.graca.api.repo.factory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import pt.graca.api.repo.mongo.MongoTransactionManager;
import pt.graca.api.repo.transaction.ITransactionManager;

import java.util.ArrayList;
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
        System.out.println();

        var lists = database.getCollection("lists");
        ArrayList<String> names = new ArrayList<>();

        System.out.println("0 - Create new list");

        if (lists.countDocuments() != 0) {
            var documents = lists.find();
            var index = 1;

            names = new ArrayList<>();

            for (var doc : documents) {
                String name = doc.getString("name");
                System.out.println(index + " - " + name);
                names.add(name);
                index++;
            }
        }

        System.out.println();
        System.out.print("Select option: ");

        int option = Integer.parseInt(scanner.nextLine());

        String listName = option == 0 ? null : names.get(option - 1);

        System.out.println("-".repeat(50));

        if (listName == null || listName.isBlank()) {
            if (option != 0) {
                throw new IllegalArgumentException("List name cannot be blank");
            } else {
                System.out.print("New list name: ");
                do {
                    listName = scanner.nextLine();
                } while (listName.isBlank());
                database.getCollection("lists").insertOne(new Document().append("name", listName).append("media",
                        new ArrayList<>()).append("users", new ArrayList<>()));
            }
        }

        return new MongoTransactionManager(mongoClient, listName);
    }
}

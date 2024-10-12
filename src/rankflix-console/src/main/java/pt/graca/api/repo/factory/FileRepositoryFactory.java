package pt.graca.api.repo.factory;

import com.google.gson.Gson;
import pt.graca.api.repo.IRepository;
import pt.graca.api.repo.file.FileRepository;
import pt.graca.api.repo.file.FileRepositoryTransaction;
import pt.graca.api.repo.file.FileTransactionManager;
import pt.graca.api.repo.transaction.ITransaction;
import pt.graca.api.repo.transaction.ITransactionManager;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static pt.graca.Utils.getUserHomePath;

public class FileRepositoryFactory extends RepositoryFactory {

    public FileRepositoryFactory(Gson gson, Scanner scanner) {
        this.gson = gson;
        this.scanner = scanner;
    }

    private final Gson gson;
    private final Scanner scanner;

    @Override
    public IRepository createRepository() {
        try {
            // scan folder for files
            String folderName = getUserHomePath().concat(File.separator).concat(".rankflix");
            File folder = new File(folderName);
            if (!folder.exists()) folder.mkdirs();

            File[] files = folder.listFiles();
            assert files != null;

            if (files.length != 0) System.out.println("Lists available:");
            else System.out.println("No lists available.");
            for (File file : files) {
                System.out.println("• " + file.getName().replace(".json", ""));
            }
            System.out.println();

            System.out.print("Create/Select list: ");
            String listName = scanner.nextLine();
            return new FileRepository(gson, folderName, listName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ITransaction createTransaction() {
        var repository = createRepository();

        if (!(repository instanceof FileRepository)) {
            throw new IllegalArgumentException("Repository must be a FileRepository");
        }

        return new FileRepositoryTransaction((FileRepository) repository);
    }

    @Override
    public ITransactionManager createTransactionManager() {
        var transaction = createTransaction();

        if (!(transaction instanceof FileRepositoryTransaction)) {
            throw new IllegalArgumentException("Transaction must be a FileRepositoryTransaction");
        }

        return new FileTransactionManager((FileRepositoryTransaction) transaction);
    }
}

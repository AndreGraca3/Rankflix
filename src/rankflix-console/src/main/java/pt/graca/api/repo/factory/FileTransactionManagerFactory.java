package pt.graca.api.repo.factory;

import com.google.gson.Gson;
import pt.graca.api.repo.file.FileTransactionManager;
import pt.graca.api.repo.transaction.ITransactionManager;

import java.io.File;
import java.util.Scanner;

import static pt.graca.Utils.getUserHomePath;

public class FileTransactionManagerFactory extends TransactionManagerFactory {

    public FileTransactionManagerFactory(Gson gson, Scanner scanner) {
        this.gson = gson;
        this.scanner = scanner;
    }

    private final Gson gson;
    private final Scanner scanner;

    @Override
    public ITransactionManager createTransactionManager() {
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
        return new FileTransactionManager(gson, folderName, listName);
    }
}

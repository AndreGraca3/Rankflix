package pt.graca;

import pt.graca.infra.generator.factory.RankGeneratorFactory;
import pt.graca.menu.MainMenuService;
import pt.graca.api.repo.factory.MongoTransactionManagerFactory;
import pt.graca.api.service.external.content.TmdbProvider;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Administrator Rankflix console :)");
        try {
            var properties = new Properties();
            properties.load(Main.class.getClassLoader().getResourceAsStream("version.properties"));
            System.out.println("Version: " + properties.getProperty("version"));
        } catch (IOException ignored) {
        }
        System.out.println("-".repeat(50));

        MainMenuService mainMenuService = getMainMenuService();
        mainMenuService.chooseList();
        mainMenuService.showForever();
    }

    private static MainMenuService getMainMenuService() {
        Scanner scanner = new Scanner(System.in);

        // repository
        // FileTransactionManagerFactory transactionManagerFactory = new FileTransactionManagerFactory(GsonSingleton.getInstance(), scanner);
        MongoTransactionManagerFactory transactionManagerFactory = new MongoTransactionManagerFactory(System.getenv("RANKFLIX_MONGO_URL"), scanner);

        // external services
        TmdbProvider contentProvider = new TmdbProvider();
        RankGeneratorFactory rankGeneratorFactory = new RankGeneratorFactory();

        // menu
        return new MainMenuService(scanner, transactionManagerFactory, contentProvider, rankGeneratorFactory);
    }
}
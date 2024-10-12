package pt.graca;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pt.graca.infra.GsonInstantTypeAdapter;
import pt.graca.menu.MainMenuService;
import pt.graca.api.repo.factory.MongoRepositoryFactory;
import pt.graca.infra.generator.ChartService;
import pt.graca.api.service.external.content.TmdbProvider;

import java.time.Instant;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Rankflix! 🍿");
        System.out.println("Version 1.0.0");
        System.out.println("-".repeat(50));

        MainMenuService mainMenuService = getMainMenuService();

        mainMenuService.chooseList();
        mainMenuService.startDiscordBot();
        // mainMenuService.show();
    }

    private static MainMenuService getMainMenuService() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new GsonInstantTypeAdapter())
                .create();
        Scanner scanner = new Scanner(System.in);

        // repository
        // FileRepositoryFactory repositoryFactory = new FileRepositoryFactory(gson, scanner);
        MongoRepositoryFactory repositoryFactory = new MongoRepositoryFactory(
                System.getenv("RANKFLIX_MONGO_URL"), scanner
        );

        // external services
        TmdbProvider contentProvider = new TmdbProvider();
        // PastebinService pastebinService = new PastebinService();
        ChartService chartService = new ChartService(gson);

        // menu
        return new MainMenuService(scanner, repositoryFactory, contentProvider, chartService);
    }
}
package pt.graca;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pt.graca.infra.GsonInstantTypeAdapter;
import pt.graca.menu.MainMenuService;
import pt.graca.menu.RankflixMenuService;
import pt.graca.repo.file.FileRepository;
import pt.graca.repo.file.FileRepositoryTransaction;
import pt.graca.repo.file.FileTransactionManager;
import pt.graca.service.external.ChartService;
import pt.graca.service.external.content.TmdbProvider;
import pt.graca.discord.bot.DiscordBotService;
import pt.graca.discord.DiscordWebhookService;
import pt.graca.service.external.PastebinService;
import pt.graca.service.RankflixService;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Scanner;

import static pt.graca.infra.OSUtils.getUserHomePath;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to Rankflix! 🍿");
        System.out.println("Version 0.1.0");
        System.out.println("-".repeat(50));

        MainMenuService mainMenuService = getMainMenuService();
        // mainMenuService.show();
        mainMenuService.startDiscordBot();
    }

    private static MainMenuService getMainMenuService() throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new GsonInstantTypeAdapter())
                .create();

        // scan folder for files
        String folderName = getUserHomePath().concat(File.separator).concat(".rankflix");
        File folder = new File(folderName);
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles();
        assert files != null;

        if (files.length != 0) System.out.println("Found the following lists:");
        else System.out.println("No existing lists found.");
        for (File file : files) {
            System.out.println("•" + file.getName().replace(".json", ""));
        }
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Create/Select list: ");
        String listName = scanner.nextLine();

        // repository
        FileRepository repository = new FileRepository(gson, folderName, listName);
        FileRepositoryTransaction transaction = new FileRepositoryTransaction(repository);

        // service
        FileTransactionManager trManager = new FileTransactionManager(transaction);
        TmdbProvider contentProvider = new TmdbProvider();
        RankflixService service = new RankflixService(trManager, contentProvider);

        // external services
        PastebinService pastebinService = new PastebinService();
        ChartService chartService = new ChartService(gson);

        // discord
        var discordWebhookUrl = System.getenv("RANKFLIX_DISCORD_WEBHOOK_URL");
        DiscordWebhookService discordWebhookService = new DiscordWebhookService(discordWebhookUrl);
        DiscordBotService discordBotService = new DiscordBotService(service, chartService);

        // menu
        RankflixMenuService rankflixMenuService = new RankflixMenuService(scanner, service, pastebinService, discordWebhookService);
        return new MainMenuService(scanner, rankflixMenuService, discordBotService);
    }
}
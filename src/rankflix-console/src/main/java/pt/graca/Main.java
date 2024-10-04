package pt.graca;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pt.graca.infra.GsonInstantTypeAdapter;
import pt.graca.menu.MenuService;
import pt.graca.repo.file.FileRepository;
import pt.graca.repo.file.FileRepositoryTransaction;
import pt.graca.repo.file.FileTransactionManager;
import pt.graca.service.ChartService;
import pt.graca.service.DiscordWebhookService;
import pt.graca.service.RankflixService;

import java.io.IOException;
import java.time.Instant;

public class Main {
    public static void main(String[] args) throws IOException {
        MenuService menuService = getMenuService();
        menuService.printMenuForever();

        // add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                menuService.sendToDiscord();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    private static MenuService getMenuService() throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new GsonInstantTypeAdapter())
                .create();

        FileRepository repository = new FileRepository(gson);
        FileRepositoryTransaction transaction = new FileRepositoryTransaction(repository);

        // service
        FileTransactionManager trManager = new FileTransactionManager(transaction);
        RankflixService service = new RankflixService(trManager);

        ChartService chartService = new ChartService(gson);

        var dummy = "https://discord.com/api/webhooks/1291205426758815830/5MztOsxaIOQ2G1NweqPiBoBbiJ6xpxBWtLHHLqoAshea2QXthW292xSjRsIGWMgmpXIr";
        DiscordWebhookService discordWebhookService = new DiscordWebhookService(dummy);

        // menu
        MenuService menuService = new MenuService(service, chartService, discordWebhookService);
        return menuService;
    }
}
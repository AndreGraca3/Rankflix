package pt.graca.menu;

import pt.graca.discord.bot.DiscordBotService;
import pt.graca.api.repo.factory.TransactionManagerFactory;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.external.content.IContentProvider;
import pt.graca.infra.generator.factory.RankGeneratorFactory;

import java.util.Scanner;

public class MainMenuService extends ConsoleMenu {

    public MainMenuService(
            Scanner scanner,
            TransactionManagerFactory transactionManagerFactory,
            IContentProvider contentProvider,
            RankGeneratorFactory rankGeneratorFactory
    ) {
        super(scanner);

        this.transactionManagerFactory = transactionManagerFactory;
        this.contentProvider = contentProvider;
        this.rankGeneratorFactory = rankGeneratorFactory;
    }

    private final TransactionManagerFactory transactionManagerFactory;
    private final IContentProvider contentProvider;
    private final RankGeneratorFactory rankGeneratorFactory;

    private RankflixMenuService rankflixMenuService;
    private DiscordBotService discordBotService;

    @ConsoleMenuOption(value = "Start discord bot service", priority = true)
    public void startDiscordBot() {
        discordBotService.start();
        System.out.println("Press enter to stop the bot");
        scanner.nextLine();
        discordBotService.stop();
    }

    @ConsoleMenuOption("Admin menu")
    public void printRankflixMenuForever() {
        rankflixMenuService.showForever();
    }

    /**
     * This method is required to be called before any other method in this class.
     */
    public void chooseList() {
        var rankflixService =
                new RankflixService(transactionManagerFactory.createTransactionManager(), contentProvider);

        // initialize user services with created RankflixService
        // with the appropriate repository with new listName
        rankflixMenuService = new RankflixMenuService(scanner, rankflixService);
        discordBotService = new DiscordBotService(rankflixService, rankGeneratorFactory);
    }
}

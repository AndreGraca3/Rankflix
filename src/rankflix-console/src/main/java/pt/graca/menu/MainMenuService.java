package pt.graca.menu;

import pt.graca.discord.bot.DiscordBotService;
import pt.graca.api.repo.factory.RepositoryFactory;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.external.content.IContentProvider;
import pt.graca.infra.generator.IRankGenerator;

import java.util.Scanner;

public class MainMenuService extends ConsoleMenu {

    public MainMenuService(
            Scanner scanner,
            RepositoryFactory repositoryFactory,
            IContentProvider contentProvider,
            IRankGenerator rankGenerator
    ) {
        super(scanner);

        this.repositoryFactory = repositoryFactory;
        this.contentProvider = contentProvider;
        this.rankGenerator = rankGenerator;
    }

    private final RepositoryFactory repositoryFactory;
    private final IContentProvider contentProvider;
    private final IRankGenerator rankGenerator;

    private RankflixMenuService rankflixMenuService;
    private DiscordBotService discordBotService;

    @ConsoleMenuOption(value = "Start discord bot service (recommended)", priority = true)
    public void startDiscordBot() {
        discordBotService.start();
    }

    @ConsoleMenuOption("Start menu service (legacy)")
    public void printRankflixMenuForever() {
        while (true) {
            rankflixMenuService.show();
        }
    }

    /**
     * This method is required to be called before any other method in this class.
     */
    public void chooseList() {
        var rankflixService =
                new RankflixService(repositoryFactory.createTransactionManager(), contentProvider);

        // initialize user services with created RankflixService
        // with the appropriate repository with new listName
        rankflixMenuService = new RankflixMenuService(scanner, rankflixService, rankGenerator);
        discordBotService = new DiscordBotService(rankflixService, rankGenerator);
    }
}

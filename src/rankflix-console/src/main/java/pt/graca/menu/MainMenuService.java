package pt.graca.menu;

import pt.graca.service.external.discord.bot.DiscordBotService;

import java.util.Scanner;

public class MainMenuService extends Menu {

    public MainMenuService(Scanner scanner, RankflixMenuService rankflixMenuService, DiscordBotService discordBotService) {
        super(scanner);

        this.rankflixMenuService = rankflixMenuService;
        this.discordBotService = discordBotService;
    }

    private final RankflixMenuService rankflixMenuService;
    private final DiscordBotService discordBotService;

    @MenuOption(value = "Start discord bot service (recommended)", priority = true)
    public void startDiscordBot() throws Exception {
        discordBotService.start();
    }

    @MenuOption("Start menu service (legacy)")
    public void printMenuForever() {
        while (true) {
            rankflixMenuService.show();
        }
    }
}

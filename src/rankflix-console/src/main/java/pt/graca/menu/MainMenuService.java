package pt.graca.menu;

import pt.graca.discord.bot.DiscordBotService;

import java.util.Scanner;

public class MainMenuService extends Menu {

    public MainMenuService(Scanner scanner, Menu rankflixMenuService, DiscordBotService discordBotService) {
        super(scanner);

        this.rankflixMenuService = rankflixMenuService;
        this.discordBotService = discordBotService;
    }

    private final Menu rankflixMenuService;
    private final DiscordBotService discordBotService;

    @MenuOption(value = "Start discord bot service (recommended)", priority = true)
    public void startDiscordBot() {
        discordBotService.start();
    }

    @MenuOption("Start menu service (legacy)")
    public void printMenuForever() {
        while (true) {
            rankflixMenuService.show();
        }
    }
}

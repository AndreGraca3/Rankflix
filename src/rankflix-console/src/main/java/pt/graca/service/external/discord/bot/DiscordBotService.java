package pt.graca.service.external.discord.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import pt.graca.service.RankflixService;
import pt.graca.service.external.discord.bot.commands.CommandManager;
import pt.graca.service.external.discord.bot.commands.DeleteMediaCommand;
import pt.graca.service.external.discord.bot.commands.NewMediaCommand;

public class DiscordBotService {

    public DiscordBotService(RankflixService rankflixService) {
        this.rankflixService = rankflixService;
    }

    private final RankflixService rankflixService;
    private JDA jda;

    public void start() throws Exception {
        var commandManager = new CommandManager();
        commandManager.add(new NewMediaCommand(rankflixService));
        commandManager.add(new DeleteMediaCommand(rankflixService));

        jda = JDABuilder
                .createDefault(System.getenv("RANKFLIX_DISCORD_BOT_TOKEN"))
                .addEventListeners(commandManager)
                .setActivity(Activity.watching(rankflixService.getCurrentListName()))
                .build();
    }
}
package pt.graca.discord.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import pt.graca.discord.bot.command.CommandManager;
import pt.graca.discord.bot.command.media.*;
import pt.graca.discord.bot.command.rank.GenerateRankCommand;
import pt.graca.discord.bot.command.rating.AddRatingCommand;
import pt.graca.discord.bot.command.rating.CheckRatingCommand;
import pt.graca.discord.bot.command.rating.DeleteRatingCommand;
import pt.graca.discord.bot.command.RankedMediaNameOptionAutoComplete;
import pt.graca.service.RankflixService;
import pt.graca.service.external.ChartService;

public class DiscordBotService {

    public DiscordBotService(RankflixService rankflixService, ChartService chartService) {
        this.rankflixService = rankflixService;
        this.chartService = chartService;
    }

    private final RankflixService rankflixService;
    private final ChartService chartService;
    private JDA jda;

    public void start() {
        var commandManager = new CommandManager();
        commandManager.add(new AddMediaByTmdbIdCommand(rankflixService));
        commandManager.add(new AddMediaBySearchCommand(rankflixService));
        commandManager.add(new DeleteMediaCommand(rankflixService));
        commandManager.add(new CheckRatingCommand(rankflixService));
        commandManager.add(new AddRatingCommand(rankflixService));
        commandManager.add(new DeleteRatingCommand(rankflixService));
        commandManager.add(new GenerateRankCommand(rankflixService, chartService));

        JDABuilder jdaBuilder = JDABuilder
                .createDefault(
                        System.getenv("RANKFLIX_DISCORD_BOT_TOKEN"),
                        GatewayIntent.GUILD_MESSAGE_REACTIONS
                )
                .addEventListeners(
                        commandManager,
                        new AddMediaUserSelectorListener(rankflixService),
                        new AddMediaBySearchAutoComplete(rankflixService),
                        new RankedMediaNameOptionAutoComplete(rankflixService)
                );

        // try to set the activity to the current list name
        try {
            jdaBuilder.setActivity(Activity.watching(rankflixService.getCurrentListName()));
        } catch (Exception e) {
            jdaBuilder.setActivity(Activity.watching("Your rankings"));
        }

        jda = jdaBuilder.build();
    }
}
package pt.graca.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import pt.graca.api.service.RankflixService;
import pt.graca.discord.command.CommandManager;
import pt.graca.discord.command.media.AddMediaBySearchCommand;
import pt.graca.discord.command.media.AddMediaByTmdbIdCommand;
import pt.graca.discord.command.media.AddMediaUserSelectorListener;
import pt.graca.discord.command.media.DeleteMediaCommand;
import pt.graca.discord.command.rank.GenerateRankCommand;
import pt.graca.discord.command.review.AddReviewCommand;
import pt.graca.discord.command.review.CheckReviewsCommand;
import pt.graca.discord.command.review.DeleteReviewCommand;
import pt.graca.discord.listeners.AutoCompleteManager;
import pt.graca.discord.listeners.MediaNameAutoComplete;
import pt.graca.discord.listeners.MediaQueryAutoComplete;
import pt.graca.infra.generator.factory.RankGeneratorFactory;

public class DiscordBotService {

    public DiscordBotService(RankflixService rankflixService, RankGeneratorFactory rankGeneratorFactory) {
        this.rankflixService = rankflixService;
        this.rankGeneratorFactory = rankGeneratorFactory;
    }

    private final RankflixService rankflixService;
    private final RankGeneratorFactory rankGeneratorFactory;
    private JDA jda;

    public void start(){
        System.out.println("Starting Discord bot...");

        var commandManager = new CommandManager();
        commandManager.add(new AddMediaByTmdbIdCommand(rankflixService));
        commandManager.add(new AddMediaBySearchCommand(rankflixService));
        commandManager.add(new DeleteMediaCommand(rankflixService));
        commandManager.add(new CheckReviewsCommand(rankflixService));
        commandManager.add(new AddReviewCommand(rankflixService));
        commandManager.add(new DeleteReviewCommand(rankflixService));
        commandManager.add(new GenerateRankCommand(rankflixService, rankGeneratorFactory));

        var autoCompletes = new AutoCompleteManager();
        autoCompletes.add(new MediaQueryAutoComplete(rankflixService));
        autoCompletes.add(new MediaNameAutoComplete(rankflixService));

        JDABuilder jdaBuilder = JDABuilder
                .createDefault(System.getenv("RANKFLIX_DISCORD_BOT_TOKEN"))
                .addEventListeners(
                        commandManager,
                        autoCompletes,
                        new AddMediaUserSelectorListener(rankflixService)
                );

        // try to set the activity to the current list name
        try {
            jdaBuilder.setActivity(Activity.watching(rankflixService.getCurrentListName()));
        } catch (Exception e) {
            jdaBuilder.setActivity(Activity.watching("Your rankings"));
        }

        jda = jdaBuilder.build();
    }

    public void stop() {
        System.out.println("Stopping Discord bot...");
        jda.shutdown();
    }
}
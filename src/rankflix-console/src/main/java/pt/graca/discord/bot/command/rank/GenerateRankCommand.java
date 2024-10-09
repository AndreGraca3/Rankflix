package pt.graca.discord.bot.command.rank;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.discord.bot.command.ICommand;
import pt.graca.service.RankflixService;
import pt.graca.service.external.ChartService;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateRankCommand implements ICommand {

    public GenerateRankCommand(RankflixService rankflixService, ChartService chartService) {
        this.rankflixService = rankflixService;
        this.chartService = chartService;
    }

    private final RankflixService rankflixService;
    private final ChartService chartService;

    @Override
    public String getName() {
        return "generate-rank";
    }

    @Override
    public String getDescription() {
        return "Generate a rank for this list's media";
    }

    @Override
    public List<OptionData> getOptions() {
        return new ArrayList<>();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        var rankedMedia = rankflixService.getTopRankedMedia(null);

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("Top " + rankedMedia.size())
                        .setDescription("Ranking for \"" + rankflixService.getCurrentListName() + "\"")
                        .setImage(chartService.generateRankingChart(rankedMedia))
                        .setColor(Color.YELLOW)
                        .build())
                .queue();
    }
}

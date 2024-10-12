package pt.graca.discord.bot.command.rank;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.discord.bot.command.ICommand;
import pt.graca.api.domain.Media;
import pt.graca.api.service.RankflixService;
import pt.graca.infra.generator.IRankGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateRankCommand implements ICommand {

    public GenerateRankCommand(RankflixService rankflixService, IRankGenerator rankGenerator) {
        this.rankflixService = rankflixService;
        this.rankGenerator = rankGenerator;
    }

    private final RankflixService rankflixService;
    private final IRankGenerator rankGenerator;

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
        List<Media> rankedMedia = rankflixService.getTopRankedMedia(null);

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                        .addField("Average", rankedMedia.stream()
                                .mapToDouble(Media::getRating)
                                .average()
                                .orElse(0) + "", true)
                        .setImage(rankGenerator.generateRankUrl(rankedMedia))
                        .setColor(Color.YELLOW)
                        .build())
                .queue();
    }
}

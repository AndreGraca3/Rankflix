package pt.graca.discord.bot.command.rank;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.rank.RankedMedia;
import pt.graca.api.service.RankflixService;
import pt.graca.discord.bot.command.ICommand;
import pt.graca.api.domain.rank.RatedMedia;
import pt.graca.infra.generator.factory.RankGeneratorFactory;
import pt.graca.infra.generator.factory.RankGeneratorType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateRankCommand implements ICommand {

    public GenerateRankCommand(RankflixService rankflixService, RankGeneratorFactory rankGeneratorFactory) {
        this.rankflixService = rankflixService;
        this.rankGeneratorFactory = rankGeneratorFactory;
    }

    private final RankflixService rankflixService;
    private final RankGeneratorFactory rankGeneratorFactory;

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
        var userOption = new OptionData(OptionType.USER,
                "user", "Generate a rank based on a specific user's watchlist");

        var generatorOption = new OptionData(OptionType.STRING,
                "generator", "The type of rank generator to use");

        for (var generatorType : RankGeneratorType.values()) {
            generatorOption.addChoice(generatorType.name().toLowerCase(), String.valueOf(generatorType));
        }

        return new ArrayList<>(List.of(userOption, generatorOption));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        var userOption = event.getOption("user");
        var userId = userOption == null
                ? null
                : rankflixService.findUserByDiscordId(userOption.getAsUser().getId()).id;

        RankedMedia rankedMedia = rankflixService.getTopRankedMedia(null, userId);

        if (rankedMedia.rankedMedia().isEmpty()) {
            throw new IllegalStateException("Media list is empty for selected ranking");
        }

        // get the generator type from the command
        OptionMapping generatorOption = event.getOption("generator");
        RankGeneratorType generatorType = generatorOption == null
                ? RankGeneratorType.GIST
                : RankGeneratorType.valueOf(generatorOption.getAsString());

        // generate rank url
        String rankUrl = rankGeneratorFactory.getRankGenerator(generatorType)
                .generateRankUrl(rankedMedia,
                        (userOption == null ? "Global" : userOption.getAsUser().getName()) + " Ranking"
                );

        // create the embed
        var discordUser = userOption == null ? null : userOption.getAsUser();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor((discordUser == null ? "Global" : discordUser.getName()) + " Rank",
                        rankUrl, discordUser == null ? null : discordUser.getAvatarUrl()
                )
                .addField("Average",
                        String.format("%.2f", rankedMedia.averageRating()), true)
                .addField("Total Ratings",
                        String.valueOf(rankedMedia.totalRatings()), true)
                .setColor(Color.YELLOW);

        if (generatorType == RankGeneratorType.CHART) {
            embedBuilder.setImage(rankUrl);
        }

        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}

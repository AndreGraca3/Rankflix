package pt.graca.discord.command.rank;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import pt.graca.api.domain.rank.RankedMedia;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.exceptions.user.UserNotFoundException;
import pt.graca.discord.command.ICommand;
import pt.graca.infra.generator.factory.RankGeneratorFactory;
import pt.graca.infra.generator.factory.RankGeneratorType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Base64;
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
        return "rank";
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
        var user = userOption == null
                ? null
                : rankflixService.findUserByDiscordId(userOption.getAsUser().getId());

        if (userOption != null && user == null) {
            throw new UserNotFoundException();
        }

        RankedMedia rankedMedia = rankflixService.getTopRankedMedia(null, user != null ? user.id : null);

        if (rankedMedia.media().isEmpty()) {
            event.getHook()
                    .sendMessageEmbeds(new EmbedBuilder()
                            .setTitle("No media found for selected ranking")
                            .setColor(Color.GRAY)
                            .build())
                    .queue();

            return;
        }

        // get the generator type from the command
        OptionMapping generatorOption = event.getOption("generator");
        RankGeneratorType generatorType = generatorOption == null
                ? RankGeneratorType.CANVAS
                : RankGeneratorType.valueOf(generatorOption.getAsString());

        // generate rank url
        String rankUrl = rankGeneratorFactory.getRankGenerator(generatorType)
                .generateRankUrl(rankedMedia,
                        (userOption == null
                                ? rankflixService.getCurrentListName() : userOption.getAsUser().getName())
                                + " Rank");

        // create the embed
        var discordUser = userOption == null ? null : userOption.getAsUser();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor((discordUser == null ? "List" : discordUser.getName()) + " Rank",
                        null, discordUser == null ? null : discordUser.getEffectiveAvatarUrl()
                )
                .addField("Average",
                        String.format("%.2f", rankedMedia.averageRating()), true)
                .addField("Total Ratings",
                        String.valueOf(rankedMedia.totalRatings()), true)
                .addField("Total watched", String.valueOf(rankedMedia.totalWatched()), true)
                .setColor(Color.YELLOW);

        switch (generatorType) {
            case CHART -> embedBuilder.setImage(rankUrl);
            case CANVAS -> embedBuilder.setImage("attachment://" + "rank.png");

            default -> embedBuilder.setUrl(rankUrl);
        }

        var m = event.getHook().sendMessageEmbeds(embedBuilder.build());

        if (generatorType == RankGeneratorType.CANVAS) {
            byte[] byteArray = Base64.getDecoder().decode(rankUrl);
            m.addFiles(FileUpload.fromData(byteArray, "rank.png"));
        }

        m.queue();
    }
}

package pt.graca.discord.command.review;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.api.domain.user.User;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.exceptions.media.MediaNotFoundException;
import pt.graca.api.service.results.MediaRatingUpdateResult;
import pt.graca.discord.command.Consts;
import pt.graca.discord.command.Consts.MEDIA_NAME_OPTION;
import pt.graca.discord.command.ICommand;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddReviewCommand implements ICommand {

    public AddReviewCommand(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public String getName() {
        return "review";
    }

    @Override
    public String getDescription() {
        return "Add your review a movie or tv show you've watched";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING,
                MEDIA_NAME_OPTION.NAME, MEDIA_NAME_OPTION.DESCRIPTION, true, true));

        var ratingOption = new OptionData(OptionType.NUMBER, Consts.RATING_OPTION.NAME, Consts.RATING_OPTION.DESCRIPTION, true);
        options.add(ratingOption);

        options.add(new OptionData(OptionType.STRING,
                Consts.COMMENT_OPTION.NAME, Consts.COMMENT_OPTION.DESCRIPTION, false));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        String mediaId = event.getOption(MEDIA_NAME_OPTION.NAME).getAsString(); // This is movieId as value labeled as MEDIA_NAME_OPTION.NAME

        float rating = (float) event.getOption(Consts.RATING_OPTION.NAME).getAsDouble();
        String comment = event.getOption("comment") != null ? event.getOption("comment").getAsString() : null;

        var media = service.findRankedMediaById(mediaId);
        if (media == null) throw new MediaNotFoundException(mediaId);

        var discordUser = event.getUser();
        User user = service.findUserByDiscordId(discordUser.getId());

        if (user == null) {
            user = service.createDiscordUser(discordUser.getId(), discordUser.getName());
        }

        MediaRatingUpdateResult ratingUpdate = service.upsertReview(user.id, mediaId, rating, comment);

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setAuthor("| Review added", null, discordUser.getEffectiveAvatarUrl())
                .setDescription(comment)
                .addField("Your rating", String.valueOf(rating), true)
                .addField("List's Rating", String.valueOf(ratingUpdate.averageRating()), true)
                .addField("Total Ratings", String.valueOf(ratingUpdate.totalRatings()), true)
                .setColor(Color.ORANGE)
                .build()
        ).queue();
    }
}

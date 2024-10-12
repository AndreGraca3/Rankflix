package pt.graca.discord.bot.command.review;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.Utils;
import pt.graca.discord.bot.command.Consts;
import pt.graca.discord.bot.command.ICommand;
import pt.graca.api.domain.Media;
import pt.graca.api.domain.Review;
import pt.graca.api.domain.User;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.exceptions.review.ReviewNotFoundException;
import pt.graca.api.service.exceptions.user.UserNotFoundException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CheckReviewCommand implements ICommand {

    public CheckReviewCommand(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public String getName() {
        return "check-review";
    }

    @Override
    public String getDescription() {
        return "Check a user's review for a movie or tv show";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER,
                Consts.MEDIA_NAME_OPTION.NAME, Consts.MEDIA_NAME_OPTION.DESCRIPTION, true, true));

        options.add(new OptionData(OptionType.USER, "user", "The user who owns the review", false));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        int mediaTmdbId = event.getOption(Consts.MEDIA_NAME_OPTION.NAME).getAsInt(); // This is movieId as value labeled as name

        var discordUser = event.getOption("user") != null
                ? event.getOption("user").getAsUser() : event.getUser();

        User user = service.findUserByDiscordId(discordUser.getId());
        if (user == null) throw new UserNotFoundException(discordUser.getId());

        Review review = service.findRating(mediaTmdbId, user.id);
        if (review == null) throw new ReviewNotFoundException(mediaTmdbId);

        Media media = service.findRankedMediaByTmdbId(mediaTmdbId);

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setAuthor("| " + user.username + "'s averageRating for \"" + media.title + "\"",
                        null, discordUser.getAvatarUrl())
                .setDescription(review.comment)
                .setColor(Color.ORANGE)
                .addField("Rating", String.valueOf(review.value), true)
                .addField("Rated At", Utils.instantToString(review.createdAt), true)
                .build()
        ).queue();
    }
}

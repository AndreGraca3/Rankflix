package pt.graca.discord.command.review;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.Utils;
import pt.graca.api.domain.Review;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.user.User;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.results.MediaDetails;
import pt.graca.discord.command.Consts;
import pt.graca.discord.command.ICommand;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckReviewsCommand implements ICommand {

    public CheckReviewsCommand(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Override
    public String getName() {
        return "check-reviews";
    }

    @Override
    public String getDescription() {
        return "Checks reviews for a specific media from everyone or from a specific user";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING,
                Consts.MEDIA_NAME_OPTION.NAME, Consts.MEDIA_NAME_OPTION.DESCRIPTION, true, true));

        options.add(new OptionData(OptionType.USER, "user", "The user who owns the review", false));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        String mediaId = event.getOption(Consts.MEDIA_NAME_OPTION.NAME).getAsString(); // This is mediaId as value labeled as MEDIA_NAME_OPTION.NAME

        var discordUser = event.getOption("user") == null
                ? null : event.getOption("user").getAsUser();

        var mediaFuture = executor.submit(() -> service.findRankedMediaById(mediaId));
        var mediaDetailsFuture = executor.submit(() -> service.getMediaDetailsById(mediaId));

        Media media = mediaFuture.get();
        MediaDetails mediaDetails = mediaDetailsFuture.get();
        if (media == null || mediaDetails == null) throw new NoSuchElementException("Media not found");

        if (discordUser != null) {
            checkReviewForUser(event, discordUser, media, mediaDetails);
        } else {
            checkReviewsForMedia(event, media, mediaDetails);
        }
    }

    private void checkReviewForUser(
            SlashCommandInteractionEvent event,
            net.dv8tion.jda.api.entities.User discordUser,
            Media media,
            MediaDetails mediaDetails) throws Exception {
        User user = service.findUserByDiscordId(discordUser.getId());
        if (user == null) throw new NoSuchElementException("User not found");

        Review review = media.getReviewByUserId(user.id);
        if (review == null) {
            throw new NoSuchElementException("User has not rated this media");
        }

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setAuthor(media.title, null, discordUser.getEffectiveAvatarUrl())
                .setThumbnail(mediaDetails.posterUrl)
                .setDescription(review.comment)
                .setColor(Color.ORANGE)
                .addField("Rating", String.valueOf(review.rating), true)
                .addField("Rated At", Utils.instantToString(review.createdAt), true)
                .build()
        ).queue();
    }

    private void checkReviewsForMedia(SlashCommandInteractionEvent event, Media media, MediaDetails mediaDetails) {
        String userRatingsString = media.watchers.parallelStream()
                .map(w -> {
                    User user;
                    try {
                        user = service.findUserById(w.userId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return String.format("%s => %s",
                            "<@" + user.discordId + ">", w.review != null ? w.review.rating : "Unrated"
                    );
                })
                .reduce("", (a, b) -> a + "\n" + b);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(mediaDetails.title)
                .setDescription(userRatingsString)
                .setThumbnail(mediaDetails.posterUrl)
                .setColor(Color.ORANGE)
                .addField("List's rating", String.valueOf(media.averageRating), true)
                .addField("Global rating",
                        String.valueOf(mediaDetails.globalRating),
                        true)
                .addField("Release Date",
                        Utils.localDateToString(mediaDetails.releaseDate),
                        true)
                .addField("Imported", String.valueOf(media.isImported), true)
                .addField("Added At", Utils.instantToString(media.createdAt), true);

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}

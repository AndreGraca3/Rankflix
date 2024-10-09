package pt.graca.discord.bot.command.rating;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.Utils;
import pt.graca.discord.bot.command.Consts;
import pt.graca.discord.bot.command.ICommand;
import pt.graca.domain.Media;
import pt.graca.domain.Rating;
import pt.graca.domain.User;
import pt.graca.service.RankflixService;
import pt.graca.service.exceptions.UserNotFoundException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CheckRatingCommand implements ICommand {

    public CheckRatingCommand(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public String getName() {
        return "check-rating";
    }

    @Override
    public String getDescription() {
        return "Check the rating of a user for a movie or tv show";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER,
                Consts.MEDIA_NAME_OPTION.NAME, Consts.MEDIA_NAME_OPTION.DESCRIPTION, true, true));

        options.add(new OptionData(OptionType.USER, "user", "The user to check the rating for", false));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        int mediaTmdbId = event.getOption(Consts.MEDIA_NAME_OPTION.NAME).getAsInt(); // This is movieId as value labeled as name

        var discordUser = event.getOption("user") != null
                ? event.getOption("user").getAsUser() : event.getUser();

        User user = service.findUserByDiscordId(discordUser.getId());
        if (user == null) throw new UserNotFoundException(discordUser.getId());

        Rating rating = service.findRating(mediaTmdbId, user.id);
        if (rating == null) throw new NoSuchElementException("Rating not found");
        Media media = service.findRankedMediaByTmdbId(mediaTmdbId);

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setAuthor("| " + user.username + "'s rating for \"" + media.title + "\"",
                        null, discordUser.getAvatarUrl())
                .setDescription(rating.comment)
                .setColor(Color.ORANGE)
                .addField("Rating", String.valueOf(rating.value), true)
                .addField("Rated At", Utils.instantToString(rating.createdAt), true)
                .build()
        ).queue();
    }
}

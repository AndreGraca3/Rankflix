package pt.graca.discord.bot.command.rating;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.discord.bot.command.ICommand;
import pt.graca.domain.User;
import pt.graca.service.RankflixService;
import pt.graca.service.exceptions.MediaNotFoundException;
import pt.graca.discord.bot.command.Consts.MEDIA_NAME_OPTION;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddRatingCommand implements ICommand {

    public AddRatingCommand(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public String getName() {
        return "rate";
    }

    @Override
    public String getDescription() {
        return "Rate a movie or tv show";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER,
                MEDIA_NAME_OPTION.NAME, MEDIA_NAME_OPTION.DESCRIPTION, true, true));

        var ratingOption = new OptionData(OptionType.NUMBER, "rating", "The rating you want to give to the media", true);
        options.add(ratingOption);

        for (float i = 0F; i <= 10F; i += 0.5F) {
            ratingOption.addChoice(String.valueOf(i), i);
        }

        options.add(new OptionData(OptionType.STRING, "comment", "A comment about the media", false));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        int mediaTmdbId = event.getOption(MEDIA_NAME_OPTION.NAME).getAsInt(); // This is movieId as value labeled as name

        float rating = (float) event.getOption("rating").getAsDouble();
        String comment = event.getOption("comment") != null ? event.getOption("comment").getAsString() : null;

        var media = service.findRankedMediaByTmdbId(mediaTmdbId);
        if (media == null) throw new MediaNotFoundException(mediaTmdbId);

        var discordUser = event.getUser();
        User user = service.findUserByDiscordId(discordUser.getId());

        if (user == null) {
            user = service.createDiscordUser(
                    discordUser.getId(), discordUser.getName(), discordUser.getAvatarUrl());
        }

        service.addRating(user.id, mediaTmdbId, rating, comment);
        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setAuthor("| Rating added", null, discordUser.getAvatarUrl())
                .setDescription(comment)
                .addField("Your rating", String.valueOf(rating), true)
                .addField("Average Rating", String.valueOf(media.getRating()), true)
                .addField("Total Ratings", String.valueOf(media.ratings.size()), true)
                .setColor(Color.ORANGE)
                .build()
        ).queue();
    }
}

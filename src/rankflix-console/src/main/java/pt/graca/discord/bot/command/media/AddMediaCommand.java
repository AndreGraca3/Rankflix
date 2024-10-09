package pt.graca.discord.bot.command.media;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import pt.graca.domain.MediaType;
import pt.graca.service.RankflixService;
import pt.graca.service.results.MediaDetails;

import java.awt.*;

public abstract class AddMediaCommand {

    public AddMediaCommand(RankflixService service) {
        this.service = service;
    }

    protected final RankflixService service;

    public void addMedia(SlashCommandInteractionEvent event, int mediaTmdbId) throws Exception {
        var member = event.getMember();

        if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
            throw new IllegalAccessException("You must be an administrator to use this command");
        }

        MediaDetails mediaDetails = service.addMedia(mediaTmdbId);

        event.getHook().sendMessageEmbeds(
                new EmbedBuilder()
                        .setTitle(mediaDetails.title())
                        .setDescription(mediaDetails.overview())
                        .setThumbnail(mediaDetails.posterUrl())
                        .addField("Type", mediaDetails.type().toString(), true)
                        .addField("TMDB ID", mediaDetails.ids().tmdbId() + "", true)
                        .addField("Global Rating", mediaDetails.globalRating().toString(), true)
                        .addField("Genres", String.join(", ", mediaDetails.genres()), true)
                        .addField("Release Date", mediaDetails.releaseDate() == null
                                ? "Sometime in the past" : mediaDetails.releaseDate().toString(), true)
                        .setColor(Color.GREEN)
                        .build()
        ).addActionRow(
                Button.link("https://www.imdb.com/title/"
                        + mediaDetails.ids().imdbId(), "IMDB"),
                Button.link("https://www.themoviedb.org/"
                        + (mediaDetails.type() == MediaType.MOVIE
                        ? "movie/" : "tv/") + mediaDetails.ids().tmdbId(), "TMDB")
        ).queue();

        EntitySelectMenu selectMenu =
                EntitySelectMenu.create("add-media-users-select:" + mediaDetails.ids().tmdbId(), EntitySelectMenu.SelectTarget.USER)
                        .setMaxValues(25) // Discord limit
                        .setPlaceholder("Pick users who watched it")
                        .build();

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("Who watched this?")
                        .setDescription("Allow users to rate this media")
                        .setColor(Color.MAGENTA)
                        .build())
                .addActionRow(selectMenu).queue();
    }
}

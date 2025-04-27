package pt.graca.discord.command.media;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import pt.graca.api.domain.media.MediaType;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.results.MediaDetails;
import pt.graca.api.service.results.MovieDetails;
import pt.graca.api.service.results.TvShowDetails;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

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

        MediaDetails mediaDetails = service.getMediaDetailsByTmdbId(mediaTmdbId);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(mediaDetails.title.substring(0, Math.min(mediaDetails.title.length(), 256)))
                .setDescription(mediaDetails.overview.substring(0, Math.min(mediaDetails.overview.length(), 4096)))
                .setThumbnail(mediaDetails.posterUrl)
                .addField("Type", mediaDetails.type.toString(), true)
                .addField("Global Rating", mediaDetails.globalRating.toString(), true)
                .addField("Genres", String.join(", ", mediaDetails.genres), true)
                .addField("Release Date", mediaDetails.releaseDate == null
                        ? "Unknown" : mediaDetails.releaseDate.toString(), true)
                .setColor(Color.GREEN);

        switch (mediaDetails) {
            case MovieDetails movieDetails -> {
                embedBuilder.addField("Budget", parseCurrency(movieDetails.budget), true);
                embedBuilder.addField("Revenue", parseCurrency(movieDetails.revenue), true);
                embedBuilder.addField("Runtime", movieDetails.runtime + " minutes", true);
            }

            case TvShowDetails showDetails -> {
                embedBuilder.addField("Seasons", String.valueOf(showDetails.seasons), true);
                embedBuilder.addField("Episodes", String.valueOf(showDetails.lastEpisodeDate), true);
            }

            default -> throw new IllegalStateException("Unexpected value: " + mediaDetails);
        }

        event.getHook().sendMessageEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.link("https://www.imdb.com/title/"
                                + mediaDetails.ids.imdbId(), "IMDB"),
                        Button.link("https://www.themoviedb.org/"
                                + (mediaDetails.type == MediaType.MOVIE
                                ? "movie/" : "tv/") + mediaDetails.ids.tmdbId(), "TMDB")
                )
                .addActionRow(
                        Button.success("confirm-media:" + mediaTmdbId, "✅"),
                        Button.danger("ignore-media", "❌")
                )
                .queue();
    }

    private String parseCurrency(Integer value) {
        if (value == null || value == 0) {
            return "Unknown";
        }
        return NumberFormat.getCurrencyInstance(Locale.US).format(value);
    }
}

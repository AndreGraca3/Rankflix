package pt.graca.service.external.discord.bot.commands;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import pt.graca.domain.MediaType;
import pt.graca.service.RankflixService;
import pt.graca.service.results.MediaResult;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NewMediaCommand implements ICommand {

    public NewMediaCommand(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public String getName() {
        return "new-media";
    }

    @Override
    public String getDescription() {
        return "Announces new media for rating";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "tmdb_id", "The TMDB ID of the media", true));
        options.add(new OptionData(OptionType.STRING, "type", "The type of media", true)
                .addChoice("Movie", "movie").addChoice("Tv Show", "tv_show"));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        String mediaTmdbId = event.getOption("tmdb_id").getAsString();
        String mediaType = event.getOption("type").getAsString();

        MediaResult media = service.createMedia(mediaTmdbId, MediaType.fromString(mediaType));

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(media.title())
                .setDescription(media.overview())
                .setThumbnail(media.posterUrl())
                .setColor(Color.GREEN)
                .build();

        event.replyEmbeds(embed).queue(m -> {
            m.retrieveOriginal().queue(message -> {
                for (int i = 1; i <= 10; i++) {
                    message.addReaction(Emoji.fromUnicode((i < 10 ? i + "\u20E3" : "\uD83D\uDD1F"))).queue();
                }
                message.addReaction(Emoji.fromUnicode("\u274C")).queue();
                message.addReaction(Emoji.fromUnicode("\u26D4")).queue();
            });
        });
    }
}

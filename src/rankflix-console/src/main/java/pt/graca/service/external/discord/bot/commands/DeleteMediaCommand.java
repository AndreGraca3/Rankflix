package pt.graca.service.external.discord.bot.commands;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.domain.MediaType;
import pt.graca.service.RankflixService;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteMediaCommand implements ICommand {

    public DeleteMediaCommand(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public String getName() {
        return "delete-media";
    }

    @Override
    public String getDescription() {
        return "Deletes media from the list";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "tmdb_id", "The TMDB ID of the media", true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        String mediaTmdbId = event.getOption("tmdb_id").getAsString();
        service.deleteMedia(mediaTmdbId);
        event.replyEmbeds(new EmbedBuilder()
                .setTitle("Media deleted")
                .setColor(Color.GRAY)
                .build()
        ).queue();
    }
}

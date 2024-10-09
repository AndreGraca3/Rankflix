package pt.graca.discord.bot.command.media;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.discord.bot.command.Consts.MEDIA_TMDB_ID_OPTION;
import pt.graca.discord.bot.command.ICommand;
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
        options.add(new OptionData(OptionType.INTEGER,
                MEDIA_TMDB_ID_OPTION.NAME, MEDIA_TMDB_ID_OPTION.DESCRIPTION, true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        int mediaTmdbId = event.getOption(MEDIA_TMDB_ID_OPTION.NAME).getAsInt();
        service.removeMediaFromRanking(mediaTmdbId);
        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Media deleted")
                .setColor(Color.GRAY)
                .build()
        ).queue();
    }
}

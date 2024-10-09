package pt.graca.discord.bot.command.media;


import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.discord.bot.command.Consts.MEDIA_TMDB_ID_OPTION;
import pt.graca.discord.bot.command.ICommand;
import pt.graca.service.RankflixService;

import java.util.ArrayList;
import java.util.List;

public class AddMediaByTmdbIdCommand extends AddMediaCommand implements ICommand {

    public AddMediaByTmdbIdCommand(RankflixService service) {
        super(service);
    }

    @Override
    public String getName() {
        return "add-media-by-tmdb-id";
    }

    @Override
    public String getDescription() {
        return "Add a new movie or tv show by its TMDB ID";
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

        addMedia(event, mediaTmdbId);
    }
}

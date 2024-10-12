package pt.graca.discord.bot.command.media;


import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.discord.bot.command.Consts.MEDIA_QUERY_OPTION;
import pt.graca.discord.bot.command.ICommand;
import pt.graca.api.service.RankflixService;

import java.util.ArrayList;
import java.util.List;

public class AddMediaBySearchCommand extends AddMediaCommand implements ICommand {

    public AddMediaBySearchCommand(RankflixService service) {
        super(service);
    }

    @Override
    public String getName() {
        return "add-media-by-search";
    }

    @Override
    public String getDescription() {
        return "Add a new movie or tv show by its name";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER,
                MEDIA_QUERY_OPTION.NAME, MEDIA_QUERY_OPTION.DESCRIPTION, true, true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        int mediaTmdbId = event.getOption(MEDIA_QUERY_OPTION.NAME).getAsInt(); // tmdb id labeled as media-query
        addMedia(event, mediaTmdbId);
    }
}

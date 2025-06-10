package pt.graca.discord.command.media;


import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.api.service.RankflixService;
import pt.graca.discord.command.Consts.MEDIA_QUERY_OPTION;

import java.util.ArrayList;
import java.util.List;

public class AddMediaBySearchCommand extends AddMediaCommand {

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
        return new ArrayList<>() {{
            add(new OptionData(OptionType.STRING,
                    MEDIA_QUERY_OPTION.NAME,
                    MEDIA_QUERY_OPTION.DESCRIPTION,
                    true,
                    true));
        }};
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        String mediaId = event.getOption(MEDIA_QUERY_OPTION.NAME).getAsString(); // id labeled as MEDIA_QUERY_OPTION.NAME
        addMedia(event, mediaId);
    }
}

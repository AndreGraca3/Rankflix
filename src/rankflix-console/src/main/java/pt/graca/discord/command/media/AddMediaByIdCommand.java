package pt.graca.discord.command.media;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.api.service.RankflixService;
import pt.graca.discord.command.Consts.MEDIA_ID_OPTION;

import java.util.ArrayList;
import java.util.List;

public class AddMediaByIdCommand extends AddMediaCommand {

    public AddMediaByIdCommand(RankflixService service) {
        super(service);
    }

    @Override
    public String getName() {
        return "add-media-by-id";
    }

    @Override
    public String getDescription() {
        return "Add a new movie or tv show by its internal ID";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING,
                MEDIA_ID_OPTION.NAME, MEDIA_ID_OPTION.DESCRIPTION, true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        String mediaId = event.getOption(MEDIA_ID_OPTION.NAME).getAsString();

        addMedia(event, mediaId);
    }
}

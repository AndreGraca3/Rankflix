package pt.graca.discord.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import pt.graca.api.domain.media.Media;
import pt.graca.api.service.RankflixService;
import pt.graca.discord.command.Consts;

import java.util.List;

public class MediaNameAutoComplete implements IAutoComplete {

    public MediaNameAutoComplete(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public String getOptionName() {
        return Consts.MEDIA_NAME_OPTION.NAME;
    }

    @Override
    public void execute(@NotNull CommandAutoCompleteInteractionEvent event) throws Exception {
        List<Media> media = service.getAllMedia(event.getFocusedOption().getValue(), 25);
        event.replyChoices(media.stream()
                        .map(m -> new Command.Choice(m.title, m.id))
                        .toList())
                .queue();
    }
}

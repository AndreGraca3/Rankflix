package pt.graca.discord.bot.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import pt.graca.domain.Media;
import pt.graca.service.RankflixService;

import java.util.List;

public class RankedMediaNameOptionAutoComplete extends ListenerAdapter {

    public RankedMediaNameOptionAutoComplete(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        try {
            if (event.getFocusedOption().getName().equals("media-name")) {
                List<Media> media = service.getAllMedia(event.getFocusedOption().getValue());
                event.replyChoices(media.stream()
                                .map(m -> new Command.Choice(m.title, m.tmdbId))
                                .toList())
                        .queue();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

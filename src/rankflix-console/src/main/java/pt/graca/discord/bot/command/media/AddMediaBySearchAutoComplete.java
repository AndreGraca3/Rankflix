package pt.graca.discord.bot.command.media;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import pt.graca.service.RankflixService;
import pt.graca.service.results.MediaDetailsItem;

import java.util.List;

public class AddMediaBySearchAutoComplete extends ListenerAdapter {

    public AddMediaBySearchAutoComplete(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        try {
            var focusedOption = event.getFocusedOption();

            if (focusedOption.getName().equals("media-query")) {
                List<MediaDetailsItem> media = service.searchMedia(focusedOption.getValue(), 1);

                event.replyChoices(
                        media.stream()
                                .map(m -> new Command.Choice(
                                        String.format("%s • %s (%s)",
                                                m.type(),
                                                m.title(),
                                                (m.releaseDate() != null ? m.releaseDate().getYear() : "Some year")),
                                        m.tmdbId()
                                )).toList()
                ).queue();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

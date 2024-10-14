package pt.graca.discord.bot.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import pt.graca.discord.bot.command.Consts;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.results.MediaDetailsItem;

import java.util.List;

public class MediaQueryAutoComplete implements IAutoComplete {

    public MediaQueryAutoComplete(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public String getOptionName() {
        return Consts.MEDIA_QUERY_OPTION.NAME;
    }

    @Override
    public void execute(@NotNull CommandAutoCompleteInteractionEvent event) {
        var focusedOption = event.getFocusedOption();

        List<MediaDetailsItem> media = service.searchMedia(focusedOption.getValue(), 1);

        event.replyChoices(
                media.stream()
                        .map(m -> new Command.Choice(
                                String.format("%s • %s (%s)",
                                        m.type(),
                                        m.title().length() > 50 ? m.title().substring(0, 50) + "..." : m.title(),
                                        (m.releaseDate() != null ? m.releaseDate().getYear() : "Some year")),
                                m.tmdbId()
                        ))
                        .limit(25)
                        .toList()
        ).queue();
    }
}

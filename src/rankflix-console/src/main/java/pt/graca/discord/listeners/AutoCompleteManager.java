package pt.graca.discord.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pt.graca.discord.command.Consts;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteManager extends ListenerAdapter {
    private final List<IAutoComplete> autoCompletes = new ArrayList<>();

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        for (IAutoComplete autoCompleter : autoCompletes) {
            try {
                if (event.getFocusedOption().getName().equals(autoCompleter.getOptionName())) {
                    autoCompleter.execute(event);
                }
            } catch (Exception e) {
                e.printStackTrace();
                event.replyChoices(List.of()).queue();
            }
        }
    }

    public void add(IAutoComplete autoComplete) {
        autoCompletes.add(autoComplete);
    }
}

package pt.graca.discord.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IAutoComplete {

    String getOptionName();

    void execute(@NotNull CommandAutoCompleteInteractionEvent event) throws Exception;
}
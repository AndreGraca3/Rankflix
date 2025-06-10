package pt.graca.discord.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface ICommand {

    String getName();

    String getDescription();

    List<OptionData> getOptions();

    default boolean isAdminCommand() {
        return false;
    }

    void execute(SlashCommandInteractionEvent event) throws Exception;

    default void checkPermission(SlashCommandInteractionEvent event) throws IllegalAccessException {
        if (!isAdminCommand()) return;

        var member = event.getMember();

        if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
            throw new IllegalAccessException("You must be an administrator to use this command");
        }
    }
}
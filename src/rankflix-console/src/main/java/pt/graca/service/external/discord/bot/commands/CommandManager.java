package pt.graca.service.external.discord.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    private final List<ICommand> commands = new ArrayList<>();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            for (ICommand command : commands) {
                guild.upsertCommand(command.getName(), command.getDescription())
                        .addOptions(command.getOptions()).queue();
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (ICommand command : commands) {
            if (command.getName().equals(event.getName())) {
                try {
                    command.execute(event);
                } catch (Exception e) {
                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription(e.getMessage())
                            .setColor(Color.RED)
                            .build()
                    ).queue();
                }
            }
        }
    }

    public void add(ICommand command) {
        commands.add(command);
    }
}
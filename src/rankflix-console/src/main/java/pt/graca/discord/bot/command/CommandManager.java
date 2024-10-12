package pt.graca.discord.bot.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pt.graca.api.service.exceptions.RankflixException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    private final List<ICommand> commands = new ArrayList<>();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        try {
            for (Guild guild : event.getJDA().getGuilds()) {
                guild.updateCommands()
                        .addCommands(commands.stream().map(cmd ->
                                Commands.slash(cmd.getName(), cmd.getDescription())
                                        .addOptions(cmd.getOptions())).toList()
                        ).queue();
            }
            System.out.println("Bot is running!");
        } catch (Exception e) {
            e.printStackTrace();
            event.getJDA().shutdown();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (ICommand command : commands) {
            if (command.getName().equals(event.getName())) {
                try {
                    event.deferReply().queue();
                    command.execute(event);
                    break;
                } catch (Exception e) {
                    if (!(e instanceof RankflixException)) e.printStackTrace();
                    event.getHook().sendMessageEmbeds(new EmbedBuilder()
                                    .setTitle("Error")
                                    .setDescription(e.getMessage() != null ? e.getMessage() : "Internal error")
                                    .setColor(Color.RED)
                                    .build()
                            )
                            .setEphemeral(true)
                            .queue();
                }
            }
        }
    }

    public void add(ICommand command) {
        commands.add(command);
    }
}
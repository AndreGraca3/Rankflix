package pt.graca.discord.command.media;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.jetbrains.annotations.NotNull;
import pt.graca.api.service.RankflixService;

import java.awt.*;

public class ConfirmMediaButtonListener extends ListenerAdapter {

    public ConfirmMediaButtonListener(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        try {
            String componentId = event.getComponentId();

            if (componentId.startsWith("confirm-media")) {
                event.deferEdit().queue();

                if (isNotAdmin(event)) {
                    throw new IllegalAccessException("You must be an administrator to use this command");
                }

                String mediaId = componentId.split(":")[1];
                service.addMedia(mediaId);

                // keep links, delete confirm buttons
                var newComponents = event.getMessage().getActionRows().getFirst();
                event.getHook().editOriginalComponents(newComponents).queue();

                sendWatchersSelectorEmbed(event, mediaId);
            } else if (componentId.equals("ignore-media")) {
                event.deferEdit().queue();

                if (isNotAdmin(event)) {
                    throw new IllegalAccessException("You must be an administrator to use this command");
                }

                event.getHook().deleteMessageById(event.getMessageId()).queue();
            }
        } catch (Exception e) {
            e.printStackTrace();

            event.getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription(e.getMessage() != null ? e.getMessage() : "Internal error")
                    .setColor(Color.RED)
                    .build()
            ).setEphemeral(true).queue();
        }
    }

    private boolean isNotAdmin(ButtonInteractionEvent event) {
        return event.getMember() == null || !event.getMember().hasPermission(Permission.ADMINISTRATOR);
    }

    private void sendWatchersSelectorEmbed(ButtonInteractionEvent event, String mediaId) {
        EntitySelectMenu selectMenu = EntitySelectMenu.create(
                        "add-media-users-select:" + mediaId,
                        EntitySelectMenu.SelectTarget.USER)
                .setMaxValues(25) // Discord limit
                .setPlaceholder("Pick users who watched it")
                .build();

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("Who watched this?")
                        .setDescription("Allow users to rate this media")
                        .setColor(Color.MAGENTA)
                        .build())
                .addActionRow(selectMenu)
                .setEphemeral(true)
                .queue();
    }
}
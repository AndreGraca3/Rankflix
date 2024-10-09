package pt.graca.discord.bot.command.media;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pt.graca.domain.User;
import pt.graca.service.RankflixService;

import java.awt.*;
import java.util.List;
import java.util.UUID;

public class AddMediaUserSelectorListener extends ListenerAdapter {

    public AddMediaUserSelectorListener(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        String componentId = event.getComponentId();

        if (componentId.startsWith("add-media-users-select")) {
            var discordUsers = event.getMentions().getUsers();
            if (discordUsers.isEmpty()) {
                event.getHook().sendMessage("You must select at least one user").queue();
            }

            int mediaTmdbId = Integer.parseInt(componentId.split(":")[1]);

            List<UUID> userIds = discordUsers.stream().map(u -> {
                var discordId = u.getId();
                try {
                    var user = service.findUserByDiscordId(discordId);
                    if (user == null) {
                        user = service.createDiscordUser(discordId, u.getName(), u.getAvatarUrl());
                    }
                    return user.id;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toList();

            try {
                service.addUsersToMedia(mediaTmdbId, userIds);
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("Users added successfully, they can now rate this media")
                        .setColor(Color.MAGENTA)
                        .build()).queue();
            } catch (Exception e) {
                e.printStackTrace();
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("An error occurred while adding users")
                        .setColor(Color.RED)
                        .build()).queue();
            } finally {
                event.editSelectMenu(event.getSelectMenu().asDisabled()).queue();
            }
        }
    }
}

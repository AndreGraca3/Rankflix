package pt.graca.discord.command.media;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pt.graca.api.service.RankflixService;

import java.awt.*;
import java.util.List;
import java.util.UUID;

public class AddMediaUserSelectorListener extends ListenerAdapter {

    public AddMediaUserSelectorListener(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        try {
            event.deferReply().queue();
            String componentId = event.getComponentId();

            if (componentId.startsWith("add-media-users-select")) {
                var discordUsers = event.getMentions().getUsers().stream().filter(u -> !u.isBot()).toList();

                if (discordUsers.isEmpty()) {
                    event.getHook().sendMessage("You must select at least one user").queue();
                }

                String mediaId = componentId.split(":")[1];

                List<UUID> userIds = discordUsers.stream().map(u -> {
                    String discordId = u.getId();

                    try {
                        var user = service.findUserByDiscordId(discordId);

                        if (user == null) {
                            user = service.createDiscordUser(discordId, u.getName());
                        }

                        return user.id;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).toList();

                service.addUsersToMedia(mediaId, userIds);

                var message = event.getMessage();
                var messageReference = message.getMessageReference();

                var embedBuilder = new EmbedBuilder()
                        .setTitle("Users added successfully")
                        .setColor(Color.MAGENTA);

                embedBuilder.addField("Eligible users to rate",
                        String.join("\n", discordUsers.stream().map(IMentionable::getAsMention).toList()),
                        true);

                if (messageReference != null) {
                    var referencedMessageId = messageReference.getMessageId();

                    event.getChannel()
                            .retrieveMessageById(referencedMessageId)
                            .queue(m -> m.replyEmbeds(embedBuilder.build()).queue());

                    event.getHook().deleteOriginal().queue();
                } else {
                    event.replyEmbeds(embedBuilder.build()).queue();
                }

                event.editSelectMenu(event.getSelectMenu().asDisabled()).queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.replyEmbeds(new EmbedBuilder()
                            .setDescription(e.getMessage())
                            .setColor(Color.RED)
                            .build()
                    )
                    .setEphemeral(true)
                    .queue();
        }
    }
}

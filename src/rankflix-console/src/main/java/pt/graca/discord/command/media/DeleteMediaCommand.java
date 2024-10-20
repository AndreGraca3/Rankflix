package pt.graca.discord.command.media;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.discord.command.Consts.MEDIA_NAME_OPTION;
import pt.graca.discord.command.ICommand;
import pt.graca.api.service.RankflixService;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteMediaCommand implements ICommand {

    public DeleteMediaCommand(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public String getName() {
        return "delete-media";
    }

    @Override
    public String getDescription() {
        return "Deletes media from the list";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER,
                MEDIA_NAME_OPTION.NAME, MEDIA_NAME_OPTION.DESCRIPTION, true, true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        var member = event.getMember();

        if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
            throw new IllegalAccessException("You must be an administrator to use this command");
        }

        int mediaTmdbId =
                event.getOption(MEDIA_NAME_OPTION.NAME).getAsInt(); // this is the tmdbId labeled as name

        var deletedMedia = service.removeMediaFromRanking(mediaTmdbId);

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(deletedMedia.title + " has been deleted from the list")
                .setColor(Color.GRAY)
                .build()
        ).queue();
    }
}

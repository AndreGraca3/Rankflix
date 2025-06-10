package pt.graca.discord.command.media;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.api.service.RankflixService;
import pt.graca.discord.command.Consts.MEDIA_NAME_OPTION;
import pt.graca.discord.command.ICommand;

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
        options.add(new OptionData(OptionType.STRING,
                MEDIA_NAME_OPTION.NAME, MEDIA_NAME_OPTION.DESCRIPTION, true, true));
        return options;
    }

    @Override
    public boolean isAdminCommand() {
        return true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        String mediaId = event.getOption(MEDIA_NAME_OPTION.NAME).getAsString(); // this is the internal id labeled as MEDIA_NAME_OPTION.NAME

        var deletedMedia = service.removeMediaFromRanking(mediaId);

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(deletedMedia.title + " has been deleted from the list")
                .setColor(Color.GRAY)
                .build()
        ).queue();
    }
}

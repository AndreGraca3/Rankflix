package pt.graca.discord.command.review;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.api.domain.user.User;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.exceptions.user.UserNotFoundException;
import pt.graca.discord.command.ICommand;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static pt.graca.discord.command.Consts.MEDIA_NAME_OPTION;

public class DeleteReviewCommand implements ICommand {

    public DeleteReviewCommand(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public String getName() {
        return "delete-review";
    }

    @Override
    public String getDescription() {
        return "Delete a review for a movie or tv show";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING,
                MEDIA_NAME_OPTION.NAME, MEDIA_NAME_OPTION.DESCRIPTION, true, true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        String mediaId = event.getOption(MEDIA_NAME_OPTION.NAME).getAsString(); // This is movieId as value labeled as MEDIA_NAME_OPTION.NAME

        var discordUser = event.getUser();
        User user = service.findUserByDiscordId(discordUser.getId());
        if (user == null) {
            throw new UserNotFoundException(discordUser.getId());
        }

        service.deleteReview(mediaId, user.id);

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setAuthor("| Rating deleted", null, discordUser.getEffectiveAvatarUrl())
                .setColor(Color.GRAY)
                .build()
        ).queue();
    }
}

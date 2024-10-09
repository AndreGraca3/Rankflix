package pt.graca.discord.bot.command.rating;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pt.graca.domain.User;
import pt.graca.service.RankflixService;
import pt.graca.service.exceptions.UserNotFoundException;
import pt.graca.discord.bot.command.ICommand;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static pt.graca.discord.bot.command.Consts.MEDIA_NAME_OPTION;

public class DeleteRatingCommand implements ICommand {

    public DeleteRatingCommand(RankflixService service) {
        this.service = service;
    }

    private final RankflixService service;

    @Override
    public String getName() {
        return "delete-rating";
    }

    @Override
    public String getDescription() {
        return "Delete a rating";
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
        int mediaTmdbId = event.getOption(MEDIA_NAME_OPTION.NAME).getAsInt(); // This is movieId as value labeled as name

        var discordUser = event.getUser();
        User user = service.findUserByDiscordId(discordUser.getId());
        if (user == null) {
            throw new UserNotFoundException(discordUser.getId());
        }

        service.deleteRating(mediaTmdbId, user.id);

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setAuthor("| Rating deleted", null, discordUser.getAvatarUrl())
                .setColor(Color.GRAY)
                .build()
        ).queue();
    }
}

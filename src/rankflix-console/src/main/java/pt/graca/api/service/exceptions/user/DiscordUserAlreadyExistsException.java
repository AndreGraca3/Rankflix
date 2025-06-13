package pt.graca.api.service.exceptions.user;

import pt.graca.api.service.exceptions.RankflixException;

public class DiscordUserAlreadyExistsException extends RankflixException {
    public DiscordUserAlreadyExistsException(String discordId) {
        super("Discord user \"" + discordId + "\" already exists", 409);
    }
}

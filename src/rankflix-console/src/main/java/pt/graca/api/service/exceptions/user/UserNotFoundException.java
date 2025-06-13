package pt.graca.api.service.exceptions.user;

import pt.graca.api.service.exceptions.RankflixException;

import java.util.UUID;

public class UserNotFoundException extends RankflixException {
    public UserNotFoundException() {
        super("User not found", 404);
    }

    public UserNotFoundException(String username) {
        super("User \"" + username + "\" not found", 404);
    }

    public UserNotFoundException(UUID userId) {
        super("User with id \"" + userId + "\" not found", 404);
    }

    public UserNotFoundException(int discordId) {
        super("User with discord id \"" + discordId + "\" not found", 404);
    }
}

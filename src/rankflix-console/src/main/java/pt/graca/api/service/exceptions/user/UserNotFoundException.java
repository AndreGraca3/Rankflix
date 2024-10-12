package pt.graca.api.service.exceptions.user;

import pt.graca.api.service.exceptions.RankflixException;

import java.util.UUID;

public class UserNotFoundException extends RankflixException {
    public UserNotFoundException(String username) {
        super("User \"" + username + "\" not found", 404);
    }

    public UserNotFoundException(UUID userId) {
        super("User with id \"" + userId + "\" not found", 404);
    }
}

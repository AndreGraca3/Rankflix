package pt.graca.service.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RankflixException {
    public UserNotFoundException(String username) {
        super("User \"" + username + "\" not found", 404);
    }

    public UserNotFoundException(UUID userId) {
        super("User with id \"" + userId + "\" not found", 404);
    }
}

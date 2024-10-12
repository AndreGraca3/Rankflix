package pt.graca.api.service.exceptions.user;

import pt.graca.api.service.exceptions.RankflixException;

public class UserAlreadyExistsException extends RankflixException {
    public UserAlreadyExistsException(String username) {
        super("User \"" + username + "\" already exists", 409);
    }
}

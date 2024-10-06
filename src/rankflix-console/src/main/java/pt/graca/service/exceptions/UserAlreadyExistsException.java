package pt.graca.service.exceptions;

public class UserAlreadyExistsException extends RankflixException {
    public UserAlreadyExistsException(String username) {
        super("User \"" + username + "\" already exists", 409);
    }
}

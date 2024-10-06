package pt.graca.service.exceptions;

public class MediaAlreadyExistsException extends RankflixException {
    public MediaAlreadyExistsException(String mediaId) {
        super("Media with id \"" + mediaId + "\" already exists", 409);
    }
}

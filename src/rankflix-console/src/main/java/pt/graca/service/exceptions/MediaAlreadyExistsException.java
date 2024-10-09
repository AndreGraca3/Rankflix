package pt.graca.service.exceptions;

public class MediaAlreadyExistsException extends RankflixException {
    public MediaAlreadyExistsException(int mediaTmdbId) {
        super("Media with id \"" + mediaTmdbId + "\" already exists", 409);
    }
}

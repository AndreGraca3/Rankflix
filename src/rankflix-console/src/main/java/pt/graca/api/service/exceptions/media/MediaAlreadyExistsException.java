package pt.graca.api.service.exceptions.media;

import pt.graca.api.service.exceptions.RankflixException;

public class MediaAlreadyExistsException extends RankflixException {
    public MediaAlreadyExistsException(int mediaTmdbId) {
        super("Media with id \"" + mediaTmdbId + "\" already exists", 409);
    }
}

package pt.graca.api.service.exceptions.media;

import pt.graca.api.service.exceptions.RankflixException;

public class MediaAlreadyExistsException extends RankflixException {
    public MediaAlreadyExistsException(String mediaId) {
        super("Media with id \"" + mediaId + "\" already exists", 409);
    }
}

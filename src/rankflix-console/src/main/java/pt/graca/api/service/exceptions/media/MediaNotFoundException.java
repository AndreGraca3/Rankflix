package pt.graca.api.service.exceptions.media;

import pt.graca.api.service.exceptions.RankflixException;

public class MediaNotFoundException extends RankflixException {
    public MediaNotFoundException(String mediaId) {
        super("Media with id \"" + mediaId + "\" not found", 404);
    }
}

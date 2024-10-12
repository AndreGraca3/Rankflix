package pt.graca.api.service.exceptions.media;

import pt.graca.api.service.exceptions.RankflixException;

public class MediaNotFoundException extends RankflixException {
    public MediaNotFoundException(int mediaTmdbId) {
        super("Media with id \"" + mediaTmdbId + "\" not found", 404);
    }
}

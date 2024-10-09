package pt.graca.service.exceptions;

public class MediaNotFoundException extends RankflixException {
    public MediaNotFoundException(int mediaTmdbId) {
        super("Media with id \"" + mediaTmdbId + "\" not found", 404);
    }
}

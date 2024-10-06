package pt.graca.service.exceptions;

public class MediaNotFoundException extends RankflixException {
    public MediaNotFoundException(String mediaId) {
        super("Media with id \"" + mediaId + "\" not found", 404);
    }
}

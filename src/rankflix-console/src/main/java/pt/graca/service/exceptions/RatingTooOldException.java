package pt.graca.service.exceptions;

public class RatingTooOldException extends RankflixException {
    public RatingTooOldException() {
        super("You can no longer change this rating", 403);
    }
}

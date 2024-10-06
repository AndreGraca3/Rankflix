package pt.graca.service.exceptions;

public class InvalidRatingException extends RankflixException {
    public InvalidRatingException(String rating) {
        super("Invalid rating: \"" + rating + "\". Rating must be a number between 0 and 10", 400);
    }
}

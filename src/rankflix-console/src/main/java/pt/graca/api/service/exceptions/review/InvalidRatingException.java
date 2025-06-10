package pt.graca.api.service.exceptions.review;

import pt.graca.api.service.exceptions.RankflixException;

public class InvalidRatingException extends RankflixException {
    public InvalidRatingException(float rating) {
        super("Invalid averageRating: \"" + rating + "\". Rating must be a number between 0 and 10", 400);
    }
}

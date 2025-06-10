package pt.graca.api.service.exceptions.review;

import pt.graca.api.service.exceptions.RankflixException;

public class InvalidReviewTargetException extends RankflixException {
    public InvalidReviewTargetException() {
        super("Reviews are not allowed on imported media", 422);
    }
}

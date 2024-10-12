package pt.graca.api.service.exceptions.review;

import pt.graca.api.service.exceptions.RankflixException;

public class UnauthorizedReviewException extends RankflixException {
    public UnauthorizedReviewException() {
        super("You are not allowed to review this media", 403);
    }
}

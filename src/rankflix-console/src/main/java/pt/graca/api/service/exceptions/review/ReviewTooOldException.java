package pt.graca.api.service.exceptions.review;

import pt.graca.api.service.exceptions.RankflixException;

public class ReviewTooOldException extends RankflixException {
    public ReviewTooOldException() {
        super("You can no longer change this review", 403);
    }
}

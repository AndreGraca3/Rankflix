package pt.graca.api.service.exceptions.review;

import pt.graca.api.service.exceptions.RankflixException;

public class ReviewNotFoundException extends RankflixException {
    public ReviewNotFoundException(String mediaId) {
        super("You dont have a review on this media", 404);
    }
}

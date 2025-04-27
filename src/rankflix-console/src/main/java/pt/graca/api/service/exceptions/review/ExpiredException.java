package pt.graca.api.service.exceptions.review;

import pt.graca.api.service.exceptions.RankflixException;

public class ExpiredException extends RankflixException {
    public ExpiredException(int maxAgeSecs) {
        super("You can no longer add or update a review for this media. The maximum allowed age is " + maxAgeSecs + " seconds", 403);
    }
}

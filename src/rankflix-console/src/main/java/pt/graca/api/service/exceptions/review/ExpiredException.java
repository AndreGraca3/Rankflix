package pt.graca.api.service.exceptions.review;

import pt.graca.api.service.exceptions.RankflixException;

import static pt.graca.Utils.formatSecondsToTime;

public class ExpiredException extends RankflixException {
    public ExpiredException(int maxAgeSecs) {
        super("You can no longer add or update a review for this media. The maximum allowed age is " +
                formatSecondsToTime(maxAgeSecs) + ".", 403);
    }
}

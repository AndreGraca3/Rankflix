package pt.graca.domain;

import pt.graca.exceptions.RankflixException;

import java.time.Instant;

public class Rating {

    public Rating(String username, float rating) throws RankflixException.InvalidRatingException {
        validateRating(rating);
        this.username = username;
        this.value = rating;
    }

    public String username;
    public float value;
    public Instant createdAt = Instant.now();

    private void validateRating(float rating) throws RankflixException.InvalidRatingException {
        if (rating < 0 || rating > 10) {
            throw new RankflixException.InvalidRatingException(rating + "");
        }
    }

    public void updateRating(float rating) throws RankflixException {
        validateRating(rating);
        if (isTooOld()) {
            throw new RankflixException.RatingTooOldException();
        }
        this.value = rating;
        this.createdAt = Instant.now();
    }

    public boolean isTooOld() {
        return Instant.now().getEpochSecond() - createdAt.getEpochSecond() > 5 * 60;
    }
}

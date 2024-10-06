package pt.graca.domain;

import pt.graca.service.exceptions.InvalidRatingException;
import pt.graca.service.exceptions.RatingTooOldException;

import java.time.Instant;
import java.util.UUID;

public class Rating {
    public Rating(UUID userId, float rating) throws InvalidRatingException {
        validateRating(rating);
        this.userId = userId;
        this.value = rating;
    }

    public UUID userId;
    public float value;
    public Instant createdAt = Instant.now();

    private void validateRating(float rating) throws InvalidRatingException {
        if (rating < 0 || rating > 10) {
            throw new InvalidRatingException(rating + "");
        }
    }

    public Rating updateRating(float rating) throws RatingTooOldException, InvalidRatingException {
        validateRating(rating);
        if (isTooOld(60 * 5)) throw new RatingTooOldException();

        return new Rating(this.userId, rating);
    }

    public boolean isTooOld(int seconds) {
        return Instant.now().getEpochSecond() - createdAt.getEpochSecond() > seconds;
    }
}

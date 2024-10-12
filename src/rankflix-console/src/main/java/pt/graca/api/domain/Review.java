package pt.graca.api.domain;

import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public class Review {

    public static final int MAX_COMMENT_LENGTH = 50;

    public Review(UUID userId, float rating, @Nullable String comment, Instant createdAt) {
        validateReview(rating, comment);
        this.userId = userId;
        this.value = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Review(UUID userId, float rating, @Nullable String comment) {
        validateReview(rating, comment);
        this.userId = userId;
        this.value = rating;
        this.comment = comment;
    }

    public final UUID userId;
    public final float value;
    public final String comment;
    public Instant createdAt = Instant.now();

    private void validateReview(float rating, @Nullable String comment) {
        if (rating < 0 || rating > 10) {
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }

        if (comment != null && comment.length() > MAX_COMMENT_LENGTH) {
            throw new IllegalArgumentException(
                    "Comment is too long, must be at most " + MAX_COMMENT_LENGTH + " characters"
            );
        }
    }

    public boolean isTooOld(int seconds) {
        return Instant.now().getEpochSecond() - createdAt.getEpochSecond() > seconds;
    }

    public Review update(float rating, @Nullable String comment) {
        validateReview(rating, comment);
        return new Review(userId, rating, comment, createdAt);
    }
}

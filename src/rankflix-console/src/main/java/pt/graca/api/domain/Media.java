package pt.graca.api.domain;

import pt.graca.api.service.exceptions.review.UnauthorizedReviewException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Media {

    public final int tmdbId;
    public final String title;
    public final float ratingSum;
    public final List<Review> reviews;
    public final List<UUID> userIds;

    public Media(int tmdbId, String title) {
        this(tmdbId, title, 0, new ArrayList<>(), new ArrayList<>());
    }

    public Media(int tmdbId, String title, float ratingSum, List<Review> reviews, List<UUID> userIds) {
        this.tmdbId = tmdbId;
        this.title = title;
        this.ratingSum = ratingSum;
        this.reviews = new ArrayList<>(reviews);
        this.userIds = new ArrayList<>(userIds);
    }

    public float getRating() {
        if (reviews.isEmpty()) {
            return 0;
        }
        return ratingSum / reviews.size();
    }

    public Media addUser(UUID userId) {
        if (userIds.contains(userId)) {
            return this;
        }

        List<UUID> newUserIds = new ArrayList<>(this.userIds);
        newUserIds.add(userId);

        return new Media(this.tmdbId, this.title, this.ratingSum, this.reviews, newUserIds);
    }

    public Media addReview(Review review) throws UnauthorizedReviewException {
        if (!userIds.contains(review.userId)) {
            throw new UnauthorizedReviewException();
        }

        List<Review> newReviews = new ArrayList<>(this.reviews);
        newReviews.add(review);

        float newRatingSum = this.ratingSum + review.value;

        return new Media(this.tmdbId, this.title, newRatingSum, newReviews, this.userIds);
    }

    public Media updateReview(Review newReview) throws UnauthorizedReviewException {
        if (!userIds.contains(newReview.userId)) {
            throw new UnauthorizedReviewException();
        }

        List<Review> newReviews = new ArrayList<>(this.reviews);
        float newRatingSum = this.ratingSum;

        for (int i = 0; i < newReviews.size(); i++) {
            var currentReview = newReviews.get(i);
            if (currentReview.userId.equals(newReview.userId)) {
                newRatingSum += newReview.value - currentReview.value;
                newReviews.set(i, newReview);
                break;
            }
        }

        return new Media(this.tmdbId, this.title, newRatingSum, newReviews, this.userIds);
    }

    public Media removeRating(UUID userId) {
        List<Review> newReviews = new ArrayList<>(this.reviews);
        float newRatingSum = this.ratingSum;

        for (int i = 0; i < newReviews.size(); i++) {
            var currentReview = newReviews.get(i);
            if (currentReview.userId.equals(userId)) {
                newRatingSum -= currentReview.value;
                newReviews.remove(i);
                break;
            }
        }

        return new Media(this.tmdbId, this.title, newRatingSum, newReviews, this.userIds);
    }
}
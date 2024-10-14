package pt.graca.api.domain.media;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.Review;
import pt.graca.api.service.exceptions.review.ReviewNotFoundException;
import pt.graca.api.service.exceptions.review.UnauthorizedReviewException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Media {

    public final int tmdbId;
    public final String title;
    public final float ratingSum;
    public final List<MediaWatcher> watchers;

    public Media(int tmdbId, String title) {
        this(tmdbId, title, 0, new ArrayList<>());
    }

    public Media(int tmdbId, String title, float ratingSum, List<MediaWatcher> watchers) {
        this.tmdbId = tmdbId;
        this.title = title;
        this.ratingSum = ratingSum;
        this.watchers = watchers;
    }

    public List<Review> getReviews() {
        return watchers.stream()
                .filter(w -> w.review != null)
                .map(w -> w.review)
                .toList();
    }

    public float getRating() {
        if (watchers.stream().noneMatch(w -> w.review != null)) {
            return 0;
        }

        return ratingSum / getReviews().size();
    }

    public @Nullable MediaWatcher getWatcher(UUID userId) {
        var a = watchers.stream()
                .filter(w -> w.userId.equals(userId))
                .findFirst();
        return a.orElse(null);
    }

    public boolean isWatchedBy(UUID userId) {
        return getWatcher(userId) != null;
    }

    public Media addUser(UUID userId) {
        if (isWatchedBy(userId)) {
            return this;
        }

        List<MediaWatcher> newWatchers = new ArrayList<>(this.watchers);
        newWatchers.add(new MediaWatcher(userId, null));

        return new Media(this.tmdbId, this.title, this.ratingSum, newWatchers);
    }

    public @Nullable Review getReview(UUID userId) {
        var watcher = getWatcher(userId);

        if (watcher == null) {
            return null;
        }

        return watcher.review;
    }

    public Media addReview(UUID userId, Review review) {
        List<MediaWatcher> newWatchers = new ArrayList<>(this.watchers);
        for (int i = 0; i < newWatchers.size(); i++) {
            var currentWatcher = newWatchers.get(i);
            if (currentWatcher.userId.equals(userId)) {
                newWatchers.set(i, new MediaWatcher(userId, review));
                break;
            }
        }

        float newRatingSum = this.ratingSum + review.rating;

        return new Media(this.tmdbId, this.title, newRatingSum, newWatchers);
    }

    public Media updateReview(UUID userId, Review newReview) throws ReviewNotFoundException {
        List<MediaWatcher> newWatchers = new ArrayList<>(this.watchers);
        float newRatingSum = this.ratingSum;

        for (int i = 0; i < newWatchers.size(); i++) {
            var currentWatcher = newWatchers.get(i);
            if (currentWatcher.userId.equals(userId)) {

                var currentReview = currentWatcher.review;
                if (currentReview == null) {
                    throw new ReviewNotFoundException(tmdbId);
                }

                newRatingSum -= currentReview.rating;
                newWatchers.set(i, new MediaWatcher(userId, newReview));

                return new Media(this.tmdbId, this.title, newRatingSum, newWatchers);
            }
        }

        throw new ReviewNotFoundException(tmdbId);
    }

    public Media removeRating(UUID userId) {
        List<MediaWatcher> newWatchers = new ArrayList<>(this.watchers);
        float newRatingSum = this.ratingSum;

        for (int i = 0; i < newWatchers.size(); i++) {
            var currentWatcher = newWatchers.get(i);
            if (currentWatcher.userId.equals(userId)) {

                var currentReview = currentWatcher.review;
                if (currentReview == null) {
                    return this;
                }

                newRatingSum -= currentReview.rating;
                newWatchers.set(i, new MediaWatcher(userId, null));

                return new Media(this.tmdbId, this.title, newRatingSum, newWatchers);
            }
        }

        return this;
    }
}
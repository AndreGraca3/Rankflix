package pt.graca.api.domain.media;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.Review;
import pt.graca.api.service.exceptions.review.UnauthorizedReviewException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Media {

    public final int tmdbId;
    public final String title;
    public final float averageRating;
    public final List<MediaWatcher> watchers;

    public Media(int tmdbId, String title) {
        this(tmdbId, title, 0, new ArrayList<>());
    }

    public Media(int tmdbId, String title, float averageRating, List<MediaWatcher> watchers) {
        this.tmdbId = tmdbId;
        this.title = title;
        this.averageRating = averageRating;
        this.watchers = watchers;
    }

    public @Nullable MediaWatcher getWatcherByUserId(UUID userId) {
        var watcher = watchers.stream()
                .filter(w -> w.userId.equals(userId))
                .findFirst();
        return watcher.orElse(null);
    }

    public boolean isWatchedBy(UUID userId) {
        return getWatcherByUserId(userId) != null;
    }

    public Media addUserAsWatcher(UUID userId) {
        if (isWatchedBy(userId)) {
            return this;
        }

        List<MediaWatcher> newWatchers = new ArrayList<>(this.watchers);
        newWatchers.add(new MediaWatcher(userId, null));

        return new Media(this.tmdbId, this.title, this.averageRating, newWatchers);
    }

    public List<Review> getReviews() {
        return watchers.stream()
                .filter(w -> w.review != null)
                .map(w -> w.review)
                .toList();
    }

    public @Nullable Review getReviewByUserId(UUID userId) {
        var watcher = getWatcherByUserId(userId);

        if (watcher == null) {
            return null;
        }

        return watcher.review;
    }

    public Media upsertReview(UUID userId, Review review) throws UnauthorizedReviewException {
        float averagesSum = 0;
        int reviewsCount = 0;
        boolean isWatcher = false;

        List<MediaWatcher> newWatchers = new ArrayList<>(watchers);

        for (int i = 0; i < watchers.size(); i++) {
            var currWatcher = watchers.get(i);

            if (currWatcher.userId.equals(userId)) {
                isWatcher = true;

                if (currWatcher.review != null) {
                    // Current watcher had a review, remove it from the sum before adding the new one
                    averagesSum -= currWatcher.review.rating;
                }
                newWatchers.set(i, new MediaWatcher(userId, review));
                averagesSum += review.rating;
                reviewsCount++;
            } else {
                // Other watchers
                if (currWatcher.review != null) {
                    averagesSum += currWatcher.review.rating;
                    reviewsCount++;
                }
            }
        }

        if (!isWatcher) {
            throw new UnauthorizedReviewException();
        }

        float newAverageRating = reviewsCount > 0 ? averagesSum / reviewsCount : 0;

        return new Media(this.tmdbId, this.title, newAverageRating, newWatchers);
    }

    public Media removeReview(UUID userId) {
        float averagesSum = 0;
        int reviewsCount = 0;

        List<MediaWatcher> newWatchers = new ArrayList<>(this.watchers);

        for (int i = 0; i < newWatchers.size(); i++) {
            var currentWatcher = newWatchers.get(i);

            if (currentWatcher.userId.equals(userId)) {
                if (currentWatcher.review != null) {
                    newWatchers.set(i, new MediaWatcher(userId, null));
                }
            } else {
                if (currentWatcher.review != null) {
                    averagesSum += currentWatcher.review.rating;
                    reviewsCount++;
                }
            }
        }

        float newAverageRating = reviewsCount > 0 ? averagesSum / reviewsCount : 0;

        return new Media(this.tmdbId, this.title, newAverageRating, newWatchers);
    }
}
package pt.graca.api.domain.media;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.Review;
import pt.graca.api.service.exceptions.review.UnauthorizedReviewException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Media {

    public final String id;
    public final String title;
    public final float averageRating;
    public final List<MediaWatcher> watchers;
    public final Instant createdAt;
    public boolean isImported = false;

    public Media(String id, String title, float averageRating, List<MediaWatcher> watchers) {
        this.id = id;
        this.title = title;
        this.averageRating = averageRating;
        this.watchers = watchers;
        this.createdAt = Instant.now();
    }

    public Media(String id, String title, float averageRating, List<MediaWatcher> watchers, boolean isImported) {
        this.id = id;
        this.title = title;
        this.averageRating = averageRating;
        this.watchers = watchers;
        this.createdAt = Instant.now();
        this.isImported = isImported;
    }

    public Media(String id, String title, float averageRating, List<MediaWatcher> watchers, Instant createdAt, boolean isImported) {
        this.id = id;
        this.title = title;
        this.averageRating = averageRating;
        this.watchers = watchers;
        this.createdAt = createdAt;
        this.isImported = isImported;
    }

    public Media(String id, String title) {
        this(id, title, 0, new ArrayList<>());
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

        return new Media(this.id, this.title, this.averageRating, newWatchers, this.createdAt, this.isImported);
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

        return new Media(this.id, this.title, newAverageRating, newWatchers, this.createdAt, this.isImported);
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

        return new Media(this.id, this.title, newAverageRating, newWatchers, this.createdAt, this.isImported);
    }

    public boolean isOlderThan(int seconds) {
        return Instant.now().getEpochSecond() - createdAt.getEpochSecond() > seconds;
    }

    public static String generateId(String externalId, MediaType mediaType) {
        return mediaType == MediaType.MOVIE ? "M-" + externalId : "S-" + externalId;
    }

    public static MediaType getType(String mediaId) {
        return mediaId.startsWith("M-") ? MediaType.MOVIE : MediaType.TV_SHOW;
    }

    public static String getExternalId(String mediaId) {
        return mediaId.substring(2);
    }
}
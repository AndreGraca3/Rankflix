package pt.graca.domain;

import pt.graca.service.exceptions.InvalidRatingException;
import pt.graca.service.exceptions.RatingTooOldException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Media {

    public Media(int tmdbId, String title) {
        this.tmdbId = tmdbId;
        this.title = title;
    }

    public int tmdbId;
    public String title;

    public float ratingSum = 0;

    public List<Rating> ratings = new ArrayList<>();

    public List<UUID> userIds = new ArrayList<>();

    public float getRating() {
        return ratingSum / ratings.size();
    }

    public Media addUser(UUID userId) {
        var newMedia = this;

        for (UUID currentUserId : newMedia.userIds) {
            if (currentUserId.equals(userId)) {
                return newMedia;
            }
        }

        newMedia.userIds.add(userId);

        return newMedia;
    }

    /**
     * Add a rating to the media or update an existing one
     *
     * @param rating the rating to add or update
     * @return the updated media
     * @throws InvalidRatingException if the rating is not between 0 and 10
     * @throws RatingTooOldException  if the rating is older than 5 minutes
     */
    public Media upsertRating(Rating rating) throws InvalidRatingException, RatingTooOldException {
        var newMedia = this;

        for (int i = 0; i < newMedia.ratings.size(); i++) {
            var currentRating = newMedia.ratings.get(i);
            if (currentRating.userId.equals(rating.userId)) {
                newMedia.ratings.set(i, currentRating.updateRating(rating.value));
                newMedia.ratingSum += rating.value - currentRating.value;
                return newMedia;
            }
        }

        newMedia.ratings.add(rating);
        newMedia.ratingSum += rating.value;

        return newMedia;
    }

    public Media removeRating(UUID userId) {
        var newMedia = this;

        Iterator<Rating> iterator = newMedia.ratings.iterator();

        while (iterator.hasNext()) {
            Rating rating = iterator.next();

            if (rating.userId.equals(userId)) {
                iterator.remove();
                newMedia.ratingSum -= rating.value;
            }
        }

        return newMedia;
    }

}

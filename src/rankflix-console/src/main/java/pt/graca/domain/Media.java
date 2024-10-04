package pt.graca.domain;

import pt.graca.exceptions.RankflixException;

import java.util.ArrayList;
import java.util.List;

public class Media {

    public Media(String imdbId, String title) {
        this.imdbId = imdbId;
        this.title = title;
    }

    public String imdbId;
    public String title;

    public float ratingSum = 0;

    public List<Rating> ratings = new ArrayList<>();

    public float getRating() {
        return ratingSum / ratings.size();
    }

    public void addRating(Rating rating) {
        ratings.add(rating);
        this.ratingSum += rating.value;
    }

    public void deleteRating(String username) throws RankflixException.RatingTooOldException {
        for (Rating rating : ratings) {
            if (rating.username.equals(username)) {
                if (rating.isTooOld()) {
                    throw new RankflixException.RatingTooOldException();
                }
                ratings.remove(rating);
                ratingSum -= rating.value;
                return;
            }
        }
    }
}

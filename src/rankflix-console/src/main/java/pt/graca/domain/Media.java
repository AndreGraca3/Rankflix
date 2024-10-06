package pt.graca.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Media {

    public Media(String tmdbId, String title) {
        this.tmdbId = tmdbId;
        this.title = title;
    }

    public String tmdbId;
    public String title;

    public float ratingSum = 0;

    public List<Rating> ratings = new ArrayList<>();

    public float getRating() {
        return ratingSum / ratings.size();
    }

    public Media addRating(Rating rating) {
        var newMedia = new Media(this.tmdbId, this.title);
        newMedia.ratingSum = this.ratingSum + rating.value;
        return newMedia;
    }

    public Media removeRating(UUID userId) {
        var newMedia = new Media(this.tmdbId, this.title);

        for (Rating rating : this.ratings) {
            if (rating.userId.equals(userId)) {
                newMedia.ratings.remove(rating);
                newMedia.ratingSum -= rating.value;
            }
        }

        return newMedia;
    }
}

package pt.graca.api.service.exceptions.review;

import pt.graca.api.service.exceptions.RankflixException;

public class ReviewAlreadyExistsException extends RankflixException {
    public ReviewAlreadyExistsException(int mediaTmdbId, String userId) {
        super("Review already exists for media " + mediaTmdbId + " and user " + userId, 409);
    }
}

package pt.graca.repo;

import pt.graca.domain.Media;
import pt.graca.domain.User;
import pt.graca.exceptions.RankflixException;

import java.util.List;

public interface IRepository {
    void createUser(String username) throws RankflixException.UserAlreadyExistsException;

    User findUser(String username);

    Media createMedia(String imdbId, String title) throws RankflixException.MediaAlreadyExistsException;

    List<Media> getAllMedia();

    Media findMedia(String mediaId);

    void deleteMedia(String mediaId);

    void deleteRating(Media media, String username) throws RankflixException.RatingTooOldException;
}

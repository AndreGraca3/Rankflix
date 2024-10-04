package pt.graca.repo.file;

import com.google.gson.Gson;
import pt.graca.domain.Media;
import pt.graca.domain.RankflixData;
import pt.graca.domain.Rating;
import pt.graca.domain.User;
import pt.graca.exceptions.RankflixException;
import pt.graca.repo.IRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static pt.graca.infra.OSUtils.getAppDataPath;

public class FileRepository implements IRepository {

    public FileRepository(Gson gson) throws IOException {
        File file = new File(getAppDataPath() + "/rankflix.json");

        if (!file.exists() || file.length() == 0) {
            rankflixData = new RankflixData();
            Files.write(file.toPath(), gson.toJson(rankflixData).getBytes());
            return;
        }

        String json = new String(Files.readAllBytes(file.toPath()));
        rankflixData = gson.fromJson(json, RankflixData.class);

        this.gson = gson;

        System.out.println("Loaded data created on " + rankflixData.creationDate);
    }

    private Gson gson;
    private final RankflixData rankflixData;

    public void createUser(String username) throws RankflixException.UserAlreadyExistsException {
        if (findUser(username) != null) {
            throw new RankflixException.UserAlreadyExistsException(username);
        }
        rankflixData.users.add(new User(username));
    }

    public Media createMedia(String imdbId, String title) throws RankflixException.MediaAlreadyExistsException {
        if (findMedia(imdbId) != null) {
            throw new RankflixException.MediaAlreadyExistsException(imdbId);
        }
        Media media = new Media(imdbId, title);
        rankflixData.media.add(media);
        return media;
    }

    public List<Media> getAllMedia() {
        return rankflixData.media;
    }

    public Media findMedia(String movie) {
        for (Media media : rankflixData.media) {
            if (media.imdbId.equals(movie)) {
                return media;
            }
        }
        return null;
    }

    @Override
    public void deleteMedia(String mediaId) {
        Media media = findMedia(mediaId);
        if (media != null) {
            rankflixData.media.remove(media);
        }
    }

    public User findUser(String username) {
        for (User user : rankflixData.users) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
    }

    public Rating addRating(User user, Media media, float userRating) throws IOException, RankflixException.InvalidRatingException {
        Rating rating = new Rating(user.username, userRating);
        media.ratings.add(rating);
        return rating;
    }

    public Rating findRating(Media media, String username) {
        for (Rating rating : media.ratings) {
            if (rating.username.equals(username)) {
                return rating;
            }
        }
        return null;
    }

    public void deleteRating(Media media, String username) throws RankflixException.RatingTooOldException {
        for (Rating rating : media.ratings) {
            if (rating.username.equals(username)) {
                if (rating.isTooOld()) {
                    throw new RankflixException.RatingTooOldException();
                }
                media.ratings.remove(rating);
                return;
            }
        }
    }

    public void saveData() throws IOException {
        String json = gson.toJson(rankflixData);
        Files.write(new File(getAppDataPath() + "/rankflix.json").toPath(), json.getBytes());
    }
}

package pt.graca.api.repo;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.Media;
import pt.graca.api.domain.Review;
import pt.graca.api.domain.User;

import java.util.List;
import java.util.UUID;

public interface IRepository {
    String getListName();

    void insertUser(User user);

    User findUserByUsername(String username);

    User findUserById(UUID userId);

    User findUserByDiscordId(String discordId);

    void insertMedia(Media media);

    List<Media> getAllSortedMediaByRating(@Nullable String query);

    Media findMediaByTmdbId(int mediaTmdbId);

    void updateMedia(Media media);

    void deleteMedia(Media media);

    Review findReview(int mediaTmdbId, UUID userId);
}

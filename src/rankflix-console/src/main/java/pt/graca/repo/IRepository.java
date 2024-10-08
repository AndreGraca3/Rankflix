package pt.graca.repo;

import org.jetbrains.annotations.Nullable;
import pt.graca.domain.Media;
import pt.graca.domain.Rating;
import pt.graca.domain.User;

import java.util.List;
import java.util.UUID;

public interface IRepository {
    String getListName();

    void insertUser(User user);

    User findUserByUsername(String username);

    User findUserById(UUID userId);

    User findUserByDiscordId(String discordId);

    void insertMedia(Media media);

    List<Media> getAllMedia(@Nullable String query);

    Media findMediaByTmdbId(int mediaTmdbId);

    void updateMedia(Media media);

    void deleteMedia(Media media);

    Rating findRating(int mediaTmdbId, UUID userId);
}

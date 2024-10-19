package pt.graca.api.repo;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.media.MediaWatcher;
import pt.graca.api.domain.user.User;

import java.util.List;
import java.util.UUID;

public interface IRepository {
    String getListName();

    void insertUser(User user);

    void updateUser(User user);

    User findUserByUsername(String username);

    User findUserById(UUID userId);

    User findUserByDiscordId(String discordId);

    void insertMedia(Media media);

    /**
     * Get all media sorted by rating
     *
     * @param query  search query to filter media
     * @param userId user id to filter media by reviews
     * @param limit  limit of media to return
     * @return list of media sorted by global rating
     */
    List<Media> getAllSortedMedia(@Nullable String query, @Nullable UUID userId, @Nullable Integer limit);

    Media findMediaByTmdbId(int mediaTmdbId);

    void updateMedia(Media media);

    void deleteMedia(Media media);

    MediaWatcher findWatcher(UUID userId, int mediaTmdbId);

    void clearAll();
}

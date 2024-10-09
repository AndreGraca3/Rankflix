package pt.graca.service;

import org.jetbrains.annotations.Nullable;
import pt.graca.domain.*;
import pt.graca.repo.transaction.ITransactionManager;
import pt.graca.service.exceptions.*;
import pt.graca.service.external.content.IContentProvider;
import pt.graca.service.results.MediaDetails;
import pt.graca.service.results.MediaDetailsItem;

import java.util.List;
import java.util.UUID;

public class RankflixService {

    public RankflixService(ITransactionManager trManager, IContentProvider contentProvider) {
        this.trManager = trManager;
        this.contentProvider = contentProvider;
    }

    private final ITransactionManager trManager;
    private final IContentProvider contentProvider;

    public String getCurrentListName() throws Exception {
        return trManager.run(ctx -> {
            return ctx.getRepository().getListName();
        });
    }

    public User createUser(String username, String avatarUrl) throws Exception {
        return trManager.run(ctx -> {
            if (ctx.getRepository().findUserByUsername(username) != null) {
                throw new UserAlreadyExistsException(username);
            }

            User user = new User(username, avatarUrl);
            ctx.getRepository().insertUser(user);
            return user;
        });
    }

    public User createDiscordUser(String discordId, String username, String avatarUrl) throws Exception {
        return trManager.run(ctx -> {
            if (ctx.getRepository().findUserByUsername(username) != null) {
                throw new UserAlreadyExistsException(username);
            }

            if (ctx.getRepository().findUserByDiscordId(discordId) != null) {
                throw new UserAlreadyExistsException(discordId);
            }

            User user = new User(discordId, username, avatarUrl);
            ctx.getRepository().insertUser(user);
            return user;
        });
    }

    public User findUserByUsername(String username) throws Exception {
        return trManager.run(ctx -> {
            var user = ctx.getRepository().findUserByUsername(username);
            if (user == null) {
                throw new UserNotFoundException(username);
            }
            return user;
        });
    }

    public User findUserById(UUID userId) throws Exception {
        return trManager.run(ctx -> {
            return ctx.getRepository().findUserById(userId);
        });
    }

    public User findUserByDiscordId(String discordId) throws Exception {
        return trManager.run(ctx -> {
            return ctx.getRepository().findUserByDiscordId(discordId);
        });
    }

    public MediaDetails getMediaDetailsByTmdbId(int mediaTmdbId) {
        return contentProvider.getMediaDetails(mediaTmdbId);
    }

    public List<MediaDetailsItem> searchMedia(String query, int page) {
        return contentProvider.searchMedia(query, page);
    }

    public MediaDetails addMedia(int mediaTmdbId) throws Exception {
        return trManager.run(ctx -> {
            if (ctx.getRepository().findMediaByTmdbId(mediaTmdbId) != null) {
                throw new MediaAlreadyExistsException(mediaTmdbId);
            }

            MediaDetails mediaDetails = contentProvider.getMediaDetails(mediaTmdbId);

            Media media = new Media(mediaTmdbId, mediaDetails.title());
            ctx.getRepository().insertMedia(media);
            return mediaDetails;
        });
    }

    public void addUsersToMedia(int mediaTmdbId, List<UUID> userIds) throws Exception {
        trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            Media updatedMedia = media;

            for (UUID userId : userIds) {
                User user = ctx.getRepository().findUserById(userId);
                if (user == null) throw new UserNotFoundException(userId);
                updatedMedia = updatedMedia.addUser(user.id);
            }

            ctx.getRepository().updateMedia(updatedMedia);
        });
    }

    public List<Media> getAllMedia(@Nullable String query) throws Exception {
        return trManager.run(ctx -> {
            return ctx.getRepository().getAllMedia(query);
        });
    }

    public List<Media> getTopRankedMedia(String query) throws Exception {
        return trManager.run(ctx -> {
            return ctx.getRepository().getAllMedia(query)
                    .stream()
                    .filter(media -> !media.ratings.isEmpty())
                    .sorted((o1, o2) -> Float.compare(o2.getRating(), o1.getRating()))
                    .toList();
        });
    }

    public Media findRankedMediaByTmdbId(int mediaTmdbId) throws Exception {
        return trManager.run(ctx -> {
            return ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
        });
    }

    public void removeMediaFromRanking(int mediaTmdbId) throws Exception {
        trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) throw new MediaNotFoundException(mediaTmdbId);
            ctx.getRepository().deleteMedia(media);
        });
    }

    public Rating findRating(int mediaTmdbId, UUID userId) throws Exception {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            if (ctx.getRepository().findUserById(userId) == null) {
                throw new UserNotFoundException(userId);
            }

            return ctx.getRepository().findRating(mediaTmdbId, userId);
        });
    }

    public void addRating(UUID userId, int mediaTmdbId, float userRating, String comment) throws Exception {
        trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            Rating rating = new Rating(userId, userRating, comment);
            Media updatedMedia = media.upsertRating(rating);
            ctx.getRepository().updateMedia(updatedMedia);
        });
    }

    public void deleteRating(int mediaTmdbId, UUID userId) throws Exception {
        trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            Media updatedMedia = media.removeRating(userId);
            ctx.getRepository().updateMedia(updatedMedia);
        });
    }
}

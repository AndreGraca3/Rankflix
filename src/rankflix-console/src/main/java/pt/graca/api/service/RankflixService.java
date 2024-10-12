package pt.graca.api.service;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.Media;
import pt.graca.api.domain.Review;
import pt.graca.api.domain.User;
import pt.graca.api.repo.transaction.ITransactionManager;
import pt.graca.api.service.exceptions.media.MediaAlreadyExistsException;
import pt.graca.api.service.exceptions.media.MediaNotFoundException;
import pt.graca.api.service.exceptions.review.ReviewAlreadyExistsException;
import pt.graca.api.service.exceptions.review.ReviewNotFoundException;
import pt.graca.api.service.exceptions.review.ReviewTooOldException;
import pt.graca.api.service.exceptions.user.UserAlreadyExistsException;
import pt.graca.api.service.exceptions.user.UserNotFoundException;
import pt.graca.api.service.external.content.IContentProvider;
import pt.graca.api.service.results.MediaDetails;
import pt.graca.api.service.results.MediaDetailsItem;
import pt.graca.api.service.results.MediaRatingUpdateResult;

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

    public List<Media> getAllSortedMediaByRating(@Nullable String query) throws Exception {
        return trManager.run(ctx -> {
            return ctx.getRepository().getAllSortedMediaByRating(query);
        });
    }

    public List<Media> getTopRankedMedia(String query) throws Exception {
        return trManager.run(ctx -> {
            return ctx.getRepository().getAllSortedMediaByRating(query)
                    .stream()
                    .filter(media -> !media.reviews.isEmpty())
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

    public Review findRating(int mediaTmdbId, UUID userId) throws Exception {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            if (ctx.getRepository().findUserById(userId) == null) {
                throw new UserNotFoundException(userId);
            }

            return ctx.getRepository().findReview(mediaTmdbId, userId);
        });
    }

    public MediaRatingUpdateResult addReview(UUID userId, int mediaTmdbId, float userRating, String comment) throws Exception {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            if (media.reviews.stream().anyMatch(review -> review.userId.equals(userId))) {
                throw new ReviewAlreadyExistsException(mediaTmdbId, userId.toString());
            }

            Review review = new Review(userId, userRating, comment);
            Media updatedMedia = media.addReview(review);
            ctx.getRepository().updateMedia(updatedMedia);

            return new MediaRatingUpdateResult(updatedMedia.tmdbId, updatedMedia.getRating(), updatedMedia.reviews.size());
        });
    }

    public MediaRatingUpdateResult updateReview(UUID userId, int mediaTmdbId, float userRating, String comment) throws Exception {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            var existingReview = media.reviews.stream()
                    .filter(review -> review.userId.equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new ReviewNotFoundException(mediaTmdbId));

            if (existingReview.isTooOld(5 * 60)) {
                throw new ReviewTooOldException();
            }

            Review updatedReview = existingReview.update(userRating, comment);
            Media updatedMedia = media.updateReview(updatedReview);
            ctx.getRepository().updateMedia(updatedMedia);

            return new MediaRatingUpdateResult(updatedMedia.tmdbId, updatedMedia.getRating(), updatedMedia.reviews.size());
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

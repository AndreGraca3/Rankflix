package pt.graca.api.service;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.Review;
import pt.graca.api.domain.media.MediaWatcher;
import pt.graca.api.domain.rank.RankedMedia;
import pt.graca.api.domain.user.User;
import pt.graca.api.repo.transaction.ITransactionManager;
import pt.graca.api.service.exceptions.media.MediaAlreadyExistsException;
import pt.graca.api.service.exceptions.media.MediaNotFoundException;
import pt.graca.api.service.exceptions.review.ReviewAlreadyExistsException;
import pt.graca.api.service.exceptions.user.UserAlreadyExistsException;
import pt.graca.api.service.exceptions.user.UserNotFoundException;
import pt.graca.api.service.external.content.IContentProvider;
import pt.graca.api.service.results.MediaDetails;
import pt.graca.api.service.results.MediaDetailsItem;
import pt.graca.api.service.results.MediaRatingUpdateResult;
import pt.graca.api.domain.rank.RatedMedia;

import java.util.List;
import java.util.Objects;
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
            return ctx.getRepository().findUserByUsername(username);
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

    public List<MediaDetailsItem> searchMedia(String query, int page) {
        return contentProvider.searchMediaByName(query, page);
    }

    public MediaDetails addMedia(int mediaTmdbId) throws Exception {
        return trManager.run(ctx -> {
            if (ctx.getRepository().findMediaByTmdbId(mediaTmdbId) != null) {
                throw new MediaAlreadyExistsException(mediaTmdbId);
            }

            MediaDetails mediaDetails = contentProvider.getMediaDetailsById(mediaTmdbId);
            if (mediaDetails == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            Media media = new Media(mediaTmdbId, mediaDetails.title);
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

    public MediaDetails addMediaWithWatchers(int mediaTmdbId, List<MediaWatcher> watchers) throws Exception {
        return trManager.run(ctx -> {
            if (ctx.getRepository().findMediaByTmdbId(mediaTmdbId) != null) {
                throw new MediaAlreadyExistsException(mediaTmdbId);
            }

            MediaDetails mediaDetails = contentProvider.getMediaDetailsById(mediaTmdbId);
            if (mediaDetails == null) throw new MediaNotFoundException(mediaTmdbId);

            Media media = new Media(mediaTmdbId, mediaDetails.title);

            for (MediaWatcher currentWatcher : watchers) {
                User user = ctx.getRepository().findUserById(currentWatcher.userId);
                if (user == null) throw new UserNotFoundException(currentWatcher.userId);

                if (currentWatcher.review != null) {
                    media = media.addUser(user.id)
                            .addReview(user.id, currentWatcher.review);
                } else {
                    media = media.addUser(user.id);
                }
            }

            ctx.getRepository().insertMedia(media);
            return mediaDetails;
        });
    }

    public List<Media> getAllMedia(@Nullable String query) throws Exception {
        return trManager.run(ctx -> {
            return ctx.getRepository().getAllSortedMedia(query, null);
        });
    }

    public RankedMedia getTopRankedMedia(@Nullable String query, @Nullable UUID userId) throws Exception {
        return trManager.run(ctx -> {
            if (userId != null && ctx.getRepository().findUserById(userId) == null) {
                throw new UserNotFoundException(userId);
            }

            List<Media> mediaList = ctx.getRepository().getAllSortedMedia(query, userId);

            final float[] totalRatingSum = {0};
            final int[] totalRatings = {0};

            var stream = mediaList.stream()
                    .filter(media -> !media.getReviews().isEmpty())
                    .filter(media -> userId == null || media.getReview(userId) != null)
                    .sorted((o1, o2) -> {
                        if (userId != null) {
                            var review1 = o1.getReview(userId);
                            var review2 = o2.getReview(userId);

                            if (review1 == null || review2 == null) {
                                return 0;
                            }

                            return Float.compare(review2.rating, review1.rating);
                        }

                        return 1;
                    })
                    .map(media -> {
                        if (userId != null) {
                            var review = media.getReview(userId);

                            if (review == null) {
                                return null;
                            }

                            totalRatingSum[0] += review.rating;
                            totalRatings[0]++;
                            return new RatedMedia(media.tmdbId, media.title, review.rating);
                        }

                        totalRatingSum[0] += media.ratingSum;
                        totalRatings[0] += media.getReviews().size();
                        return new RatedMedia(media.tmdbId, media.title, media.getRating());
                    })
                    .filter(Objects::nonNull);

            return new RankedMedia(stream.toList(), totalRatingSum[0] / totalRatings[0], totalRatings[0]);
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

    public Review findReview(int mediaTmdbId, UUID userId) throws Exception {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            if (ctx.getRepository().findUserById(userId) == null) {
                throw new UserNotFoundException(userId);
            }

            return media.getReview(userId);
        });
    }

    public MediaRatingUpdateResult addReview(UUID userId, int mediaTmdbId, float userRating, String comment) throws Exception {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            if (media.getReview(userId) == null) {
                throw new ReviewAlreadyExistsException(mediaTmdbId, userId.toString());
            }

            Review review = new Review(userRating, comment);
            Media updatedMedia = media.addReview(userId, review);
            ctx.getRepository().updateMedia(updatedMedia);

            return new MediaRatingUpdateResult(updatedMedia.tmdbId, updatedMedia.getRating(),
                    updatedMedia.getReviews().size());
        });
    }

    public MediaRatingUpdateResult updateReview(UUID userId, int mediaTmdbId, float userRating, String comment) throws Exception {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            Review review = new Review(userRating, comment);
            Media updatedMedia = media.updateReview(userId, review);
            ctx.getRepository().updateMedia(updatedMedia);

            return new MediaRatingUpdateResult(updatedMedia.tmdbId, updatedMedia.getRating(),
                    updatedMedia.getReviews().size());
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

    public void clearAll() throws Exception {
        trManager.run(ctx -> {
            ctx.getRepository().clearAll();
        });
    }
}

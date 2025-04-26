package pt.graca.api.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.Review;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.media.MediaWatcher;
import pt.graca.api.domain.rank.RankedMedia;
import pt.graca.api.domain.rank.RatedMedia;
import pt.graca.api.domain.user.User;
import pt.graca.api.repo.transaction.ITransactionManager;
import pt.graca.api.service.exceptions.RankflixException;
import pt.graca.api.service.exceptions.media.MediaAlreadyExistsException;
import pt.graca.api.service.exceptions.media.MediaNotFoundException;
import pt.graca.api.service.exceptions.review.ReviewAlreadyExistsException;
import pt.graca.api.service.exceptions.review.ReviewNotFoundException;
import pt.graca.api.service.exceptions.review.ReviewTooOldException;
import pt.graca.api.service.exceptions.review.UnauthorizedReviewException;
import pt.graca.api.service.exceptions.user.UserAlreadyExistsException;
import pt.graca.api.service.exceptions.user.UserNotFoundException;
import pt.graca.api.service.external.content.IContentProvider;
import pt.graca.api.service.results.MediaDetails;
import pt.graca.api.service.results.MediaDetailsItem;
import pt.graca.api.service.results.MediaRatingUpdateResult;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class RankflixService {

    public RankflixService(ITransactionManager trManager, IContentProvider contentProvider) {
        this.trManager = trManager;
        this.contentProvider = contentProvider;
    }

    private final ITransactionManager trManager;
    private final IContentProvider contentProvider;

    private static final int MAX_REVIEW_AGE_SECS = 5 * 60; // 5 minutes

    public String getCurrentListName() throws RankflixException {
        return trManager.run(ctx -> {
            return ctx.getRepository().getListName();
        });
    }

    public User createUser(String username, String avatarUrl) throws RankflixException {
        return trManager.run(ctx -> {
            if (ctx.getRepository().findUserByUsername(username) != null) {
                throw new UserAlreadyExistsException(username);
            }

            User user = new User(username, avatarUrl);
            ctx.getRepository().insertUser(user);
            return user;
        });
    }

    public User createDiscordUser(String discordId, String username) throws RankflixException {
        return trManager.run(ctx -> {
            if (ctx.getRepository().findUserByUsername(username) != null) {
                throw new UserAlreadyExistsException(username);
            }

            if (ctx.getRepository().findUserByDiscordId(discordId) != null) {
                throw new UserAlreadyExistsException(discordId);
            }

            User user = new User(discordId, username);
            ctx.getRepository().insertUser(user);
            return user;
        });
    }

    public List<User> getAllUsers() throws RankflixException {
        return trManager.run(ctx -> {
            return ctx.getRepository().getAllUsers();
        });
    }

    public User findUserByUsername(String username) throws RankflixException {
        return trManager.run(ctx -> {
            return ctx.getRepository().findUserByUsername(username);
        });
    }

    public User findUserById(UUID userId) throws RankflixException {
        return trManager.run(ctx -> {
            return ctx.getRepository().findUserById(userId);
        });
    }

    public User findUserByDiscordId(String discordId) throws RankflixException {
        return trManager.run(ctx -> {
            return ctx.getRepository().findUserByDiscordId(discordId);
        });
    }

    public void updateUser(UUID userId, @Nullable String newUsername, @Nullable String newDiscordId) throws RankflixException {
        trManager.run(ctx -> {
            User user = ctx.getRepository().findUserById(userId);
            if (user == null) {
                throw new UserNotFoundException(userId);
            }

            User updatedUser = user.updateUser(newUsername, newDiscordId);
            ctx.getRepository().updateUser(updatedUser);
        });
    }

    public void deleteAllUsers() throws RankflixException {
        trManager.run(ctx -> {
            ctx.getRepository().deleteAllUsers();
        });
    }

    public MediaDetails getMediaDetailsByTmdbId(int mediaTmdbId) {
        return contentProvider.getMediaDetailsById(mediaTmdbId);
    }

    public List<MediaDetailsItem> searchMediaDetailsByName(String query, int page) {
        return contentProvider.searchMediaByName(query, page);
    }

    public MediaDetails addMedia(int mediaTmdbId) throws RankflixException {
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

    public void addUsersToMedia(int mediaTmdbId, List<UUID> userIds) throws RankflixException {
        trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            Media updatedMedia = media;

            for (UUID userId : userIds) {
                User user = ctx.getRepository().findUserById(userId);
                if (user == null) throw new UserNotFoundException(userId);
                updatedMedia = updatedMedia.addUserAsWatcher(user.id);
            }

            ctx.getRepository().updateMedia(updatedMedia);
        });
    }

    public void importMediasWithWatchersRange(List<Media> mediasToInsert, List<User> users) throws RankflixException {
        trManager.run(ctx -> {
            List<Media> mediaWithCorrectTitles = new ArrayList<>();
            Map<UUID, User> usersMap = new HashMap<>();

            for (User user : users) {
                usersMap.put(user.id, user);
            }

            for (Media media : mediasToInsert) {
                MediaDetails mediaDetails = contentProvider.getMediaDetailsById(media.tmdbId);
                if (mediaDetails == null) throw new MediaNotFoundException(media.tmdbId);

                media = new Media(media.tmdbId, mediaDetails.title, media.averageRating, media.watchers);

                for (MediaWatcher currentWatcher : media.watchers) {
                    var user = usersMap.get(currentWatcher.userId);

                    if (user == null) {
                        throw new UserNotFoundException(currentWatcher.userId);
                    }
                }

                mediaWithCorrectTitles.add(media);
            }

            ctx.getRepository().insertUserRange(users);
            ctx.getRepository().insertMediaRange(mediaWithCorrectTitles);
        });
    }

    public List<Media> getAllMedia(@Nullable String query, @Nullable int limit) throws RankflixException {
        return trManager.run(ctx -> {
            return ctx.getRepository().getAllSortedMedia(query, null, limit);
        });
    }

    public RankedMedia getTopRankedMedia(@Nullable String searchQuery, @Nullable UUID userId) throws RankflixException {
        return trManager.run(ctx -> {
            if (userId != null && ctx.getRepository().findUserById(userId) == null) {
                throw new UserNotFoundException(userId);
            }

            List<Media> mediaList = ctx.getRepository().getAllSortedMedia(searchQuery, userId, null);

            var mediaStream = getMediaStream(userId, mediaList);

            AtomicReference<Float> totalAverageRating = new AtomicReference<>(0f);
            AtomicInteger totalRatings = new AtomicInteger(0);

            var ratedMedia = mediaStream.map(media -> {
                        if (userId != null) {
                            var userReview = media.getReviewByUserId(userId);

                            if (userReview == null) {
                                return null;
                            }

                            totalAverageRating.updateAndGet(v -> v + userReview.rating);
                            totalRatings.incrementAndGet();

                            return new RatedMedia(media.tmdbId, media.title, userReview.rating);
                        }

                        totalAverageRating.updateAndGet(v -> v + media.averageRating);
                        totalRatings.addAndGet(media.getReviews().size());

                        return new RatedMedia(media.tmdbId, media.title, media.averageRating);
                    })
                    .filter(Objects::nonNull)
                    .toList();

            return new RankedMedia(ratedMedia, totalAverageRating.get() / ratedMedia.size(), totalRatings.get());
        });
    }

    // helper method to get a stream of media sorted by user rating
    @NotNull
    private static Stream<Media> getMediaStream(UUID userId, List<Media> mediaList) {
        var mediaStream = mediaList.stream();

        if (userId != null) {
            mediaStream = mediaStream.sorted(
                    Comparator.comparing(
                            (Media media) -> {
                                var review = media.getReviewByUserId(userId);
                                return review != null ? review.rating : null;
                            },
                            Comparator.nullsLast(Comparator.reverseOrder())
                    )
            );
        }
        return mediaStream;
    }

    public Media findRankedMediaByTmdbId(int mediaTmdbId) throws RankflixException {
        return trManager.run(ctx -> {
            return ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
        });
    }

    public Media removeMediaFromRanking(int mediaTmdbId) throws Exception {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) throw new MediaNotFoundException(mediaTmdbId);
            ctx.getRepository().deleteMedia(media);
            return media;
        });
    }

    public MediaRatingUpdateResult addReview(
            UUID userId,
            int mediaTmdbId,
            float userRating,
            String comment
    ) throws RankflixException {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            MediaWatcher watcher = media.getWatcherByUserId(userId);
            if (watcher == null) {
                throw new UnauthorizedReviewException();
            }

            if (watcher.review != null) {
                throw new ReviewAlreadyExistsException(mediaTmdbId, userId.toString());
            }

            Review review = new Review(userRating, comment);
            Media updatedMedia = media.upsertReview(userId, review);
            ctx.getRepository().updateMedia(updatedMedia);

            return new MediaRatingUpdateResult(
                    updatedMedia.tmdbId,
                    updatedMedia.averageRating,
                    updatedMedia.getReviews().size()
            );
        });
    }

    public MediaRatingUpdateResult updateReview(
            UUID userId,
            int mediaTmdbId,
            float userRating,
            String comment
    ) throws RankflixException {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            Review existingReview = media.getReviewByUserId(userId);
            if (existingReview == null) {
                throw new ReviewNotFoundException(mediaTmdbId);
            }

            if (existingReview.isOlderThan(MAX_REVIEW_AGE_SECS)) {
                throw new ReviewTooOldException();
            }

            Review updatedReview = existingReview.update(userRating, comment);

            Media updatedMedia = media.upsertReview(userId, updatedReview);
            ctx.getRepository().updateMedia(updatedMedia);

            return new MediaRatingUpdateResult(
                    updatedMedia.tmdbId,
                    updatedMedia.averageRating,
                    updatedMedia.getReviews().size()
            );
        });
    }

    public void deleteReview(int mediaTmdbId, UUID userId) throws RankflixException {
        trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            Review existingReview = media.getReviewByUserId(userId);
            if (existingReview == null) {
                throw new ReviewNotFoundException(mediaTmdbId);
            }

            if (existingReview.isOlderThan(MAX_REVIEW_AGE_SECS)) {
                throw new ReviewTooOldException();
            }

            Media updatedMedia = media.removeReview(userId);
            ctx.getRepository().updateMedia(updatedMedia);
        });
    }

    public void forceDeleteReview(int mediaTmdbId, UUID userId) throws RankflixException {
        trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                throw new MediaNotFoundException(mediaTmdbId);
            }

            Review existingReview = media.getReviewByUserId(userId);
            if (existingReview == null) {
                throw new ReviewAlreadyExistsException(mediaTmdbId, userId.toString());
            }

            Media updatedMedia = media.removeReview(userId);
            ctx.getRepository().updateMedia(updatedMedia);
        });
    }

    public void clearList() throws RankflixException {
        trManager.run(ctx -> {
            ctx.getRepository().clearList();
        });
    }
}

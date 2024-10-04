package pt.graca.service;

import pt.graca.domain.Media;
import pt.graca.domain.Rating;
import pt.graca.domain.User;
import pt.graca.exceptions.RankflixException;
import pt.graca.repo.transaction.ITransactionManager;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RankflixService {

    public RankflixService(ITransactionManager trManager) {
        this.trManager = trManager;
    }

    private final ITransactionManager trManager;

    public void createUser(String username) throws Exception {
        trManager.run(ctx -> {
            ctx.getRepository().createUser(username);
            return null;
        });
    }

    public User findUserOrThrow(String username) throws Exception {

        return trManager.run(ctx -> {
            User user = ctx.getRepository().findUser(username);
            if (user == null) {
                throw new RankflixException.UserNotFoundException(username);
            }
            return user;
        });
    }

    public Media createMedia(String imdbId, String title) throws Exception {
        return trManager.run(ctx -> ctx.getRepository().createMedia(imdbId, title));
    }

    public List<Media> getAllMedia() throws Exception {
        return trManager.run(ctx -> ctx.getRepository().getAllMedia());
    }

    public Media findMedia(String mediaId) throws Exception {
        return trManager.run(ctx -> ctx.getRepository().findMedia(mediaId));
    }

    public Media findMediaOrThrow(String movie) throws Exception {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMedia(movie);
            if (media == null) {
                throw new RankflixException.MediaNotFoundException(movie);
            }
            return media;
        });
    }

    public void addRating(User user, String mediaId, float userRating, Supplier<String> mediaTitleSupplier) throws Exception {
        trManager.run(ctx -> {
            Media media = ctx.getRepository().findMedia(mediaId);
            if (media == null) {
                media = ctx.getRepository().createMedia(mediaId, mediaTitleSupplier.get());
            }

            Rating rating = new Rating(user.username, userRating);
            for (Rating r : media.ratings) {
                if (r.username.equals(user.username)) {
                    r.updateRating(userRating);
                    return null;
                }
            }
            media.ratings.add(rating);
            media.ratingSum += userRating;

            return null;
        });
    }

    public void addMultipleRatings(String mediaId, Map<String, Float> userRatings, Supplier<String> mediaTitleSupplier) throws Exception {
        trManager.run(ctx -> {
            Media media = ctx.getRepository().findMedia(mediaId);
            String mediaTitle = media != null ? media.title : mediaTitleSupplier.get();
            if (media == null) {
                ctx.getRepository().createMedia(mediaId, mediaTitle);
            }

            for (String username : userRatings.keySet()) {
                User user = ctx.getRepository().findUser(username);
                if (user == null) {
                    throw new RankflixException.UserNotFoundException(username);
                }
                addRating(user, mediaId, userRatings.get(username), () -> mediaTitle);
            }
            return null;
        });
    }

    public void deleteRating(Media media, String username) throws Exception {
        trManager.run(ctx -> {
            ctx.getRepository().deleteRating(media, username);
            if (media.ratings.isEmpty()) {
                ctx.getRepository().deleteMedia(media.imdbId);
            }
            return null;
        });
    }
}

package pt.graca.service;

import pt.graca.domain.Media;
import pt.graca.domain.MediaType;
import pt.graca.domain.Rating;
import pt.graca.domain.User;
import pt.graca.repo.transaction.ITransactionManager;
import pt.graca.service.exceptions.*;
import pt.graca.service.external.content.IContentProvider;
import pt.graca.service.results.MediaResult;

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

    public User createUser(String username) throws Exception {
        return trManager.run(ctx -> {
            if (ctx.getRepository().findUserByUsername(username) != null) {
                throw new UserAlreadyExistsException(username);
            }

            User user = new User(username);
            ctx.getRepository().insertUser(user);
            return user;
        });
    }

    public User createDiscordUser(String discordId, String username) throws Exception {
        return trManager.run(ctx -> {
            if (ctx.getRepository().findUserByUsername(username) != null) {
                throw new UserAlreadyExistsException(username);
            }

            if (ctx.getRepository().findUserByDiscordId(discordId) != null) {
                throw new UserAlreadyExistsException(discordId); // wrong
            }

            User user = new User(discordId, username);
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
            var user = ctx.getRepository().findUserById(userId);
            if (user == null) throw new UserNotFoundException(userId);
            return user;
        });
    }

    public User findUserByDiscordId(String discordId) throws Exception {
        return trManager.run(ctx -> {
            var user = ctx.getRepository().findUserByDiscordId(discordId);
            if (user == null) throw new UserNotFoundException(discordId);
            return user;
        });
    }

    public MediaResult createMedia(String mediaTmdbId, MediaType mediaType) throws Exception {
        return trManager.run(ctx -> {
            if (ctx.getRepository().findMediaByTmdbId(mediaTmdbId) != null) {
                throw new MediaAlreadyExistsException(mediaTmdbId);
            }

            MediaResult mediaResult = (mediaType == MediaType.MOVIE)
                    ? contentProvider.getMovieDetails(mediaTmdbId)
                    : contentProvider.getTvShowDetails(mediaTmdbId);

            Media media = new Media(mediaTmdbId, mediaResult.title());
            ctx.getRepository().insertMedia(media);
            return mediaResult;
        });
    }

    public List<Media> getAllMedia() throws Exception {
        return trManager.run(ctx -> {
            return ctx.getRepository().getAllMedia();
        });
    }

    public Media findMediaByTmdbId(String mediaTmdbId) throws Exception {
        return trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) throw new MediaNotFoundException(mediaTmdbId);
            return media;
        });
    }

    public void deleteMedia(String mediaTmdbId) throws Exception {
        trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) throw new MediaNotFoundException(mediaTmdbId);
            ctx.getRepository().deleteMedia(media);
        });
    }

    public void addRating(User user, String mediaTmdbId, float userRating) throws Exception {
        trManager.run(ctx -> {
            Media media = ctx.getRepository().findMediaByTmdbId(mediaTmdbId);
            if (media == null) {
                // tmdb api stuff

                ctx.getRepository().insertMedia(new Media(mediaTmdbId, "Title"));
            }

            Rating rating = new Rating(user.userId, userRating);
            Media updatedMedia = media.addRating(rating);
            ctx.getRepository().updateMedia(updatedMedia);
        });
    }

    public void deleteRating(String mediaTmdbId, UUID userId) throws Exception {
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

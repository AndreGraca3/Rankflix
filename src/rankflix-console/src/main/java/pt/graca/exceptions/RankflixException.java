package pt.graca.exceptions;

public class RankflixException extends Exception {
    public RankflixException(String message) {
        super(message);
    }

    public static class UserAlreadyExistsException extends RankflixException {
        public UserAlreadyExistsException(String username) {
            super("User \"" + username + "\" already exists");
        }
    }

    public static class UserNotFoundException extends RankflixException {
        public UserNotFoundException(String username) {
            super("User \"" + username + "\" not found");
        }
    }

    public static class MediaNotFoundException extends RankflixException {
        public MediaNotFoundException(String mediaId) {
            super("Media with id \"" + mediaId + "\" not found");
        }
    }

    public static class MediaAlreadyExistsException extends RankflixException {
        public MediaAlreadyExistsException(String mediaId) {
            super("Media with id \"" + mediaId + "\" already exists");
        }
    }

    public static class RatingNotFoundException extends RankflixException {
        public RatingNotFoundException() {
            super("Rating not found");
        }
    }

    public static class RatingTooOldException extends RankflixException {
        public RatingTooOldException() {
            super("You can no longer change this rating");
        }
    }

    public static class InvalidRatingException extends RankflixException {
        public InvalidRatingException(String rating) {
            super("Invalid rating: \"" + rating + "\". Rating must be a number between 0 and 10");
        }
    }
}


package pt.graca.api.service.exceptions;

public class RankflixException extends Exception {
    public RankflixException(String message, int statusCode) {
        super(message);
    }
}
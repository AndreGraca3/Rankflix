package pt.graca.service.exceptions;

class RankflixException extends Exception {
    public RankflixException(String message, int statusCode) {
        super(message);
    }
}
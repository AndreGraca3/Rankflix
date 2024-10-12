package pt.graca.api.domain;

public enum MediaType {
    MOVIE("movie"),
    TV_SHOW("tv");

    private final String type;

    MediaType(String type) {
        this.type = type;
    }

    public static MediaType fromString(String type) {
        for (MediaType mediaType : MediaType.values()) {
            if (mediaType.type.equalsIgnoreCase(type)) {
                return mediaType;
            }
        }
        throw new IllegalArgumentException("Invalid media type: " + type);
    }

    @Override
    public String toString() {
        return this.type.toUpperCase();
    }
}


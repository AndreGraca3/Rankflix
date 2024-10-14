package pt.graca.api.domain.media;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.Review;

import java.util.UUID;

public class MediaWatcher {
    public MediaWatcher(UUID userId, @Nullable Review review) {
        this.userId = userId;
        this.review = review;
    }

    public UUID userId;

    @Nullable
    public Review review;
}

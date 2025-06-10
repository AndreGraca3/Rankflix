package pt.graca.infra.content;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.service.results.MediaDetails;
import pt.graca.api.service.results.MediaDetailsItem;

import java.util.List;

public interface IContentProvider {
    @Nullable
    MediaDetails getMediaDetailsById(String mediaId);

    List<MediaDetailsItem> searchMediaByName(String query, int page);
}

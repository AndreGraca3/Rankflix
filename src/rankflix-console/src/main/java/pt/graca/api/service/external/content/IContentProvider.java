package pt.graca.api.service.external.content;

import org.jetbrains.annotations.Nullable;
import pt.graca.api.service.results.MediaDetails;
import pt.graca.api.service.results.MediaDetailsItem;

import java.util.List;

public interface IContentProvider {
    @Nullable
    MediaDetails getMediaDetailsById(int id);

    List<MediaDetailsItem> searchMediaByName(String query, int page);
}

package pt.graca.api.service.external.content;

import pt.graca.api.service.results.MediaDetails;
import pt.graca.api.service.results.MediaDetailsItem;

import java.util.List;

public interface IContentProvider {
    MediaDetails getMediaDetails(int id);

    List<MediaDetailsItem> searchMedia(String query, int page);
}

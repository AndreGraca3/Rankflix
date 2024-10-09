package pt.graca.service.external.content;

import pt.graca.service.results.MediaDetailsItem;
import pt.graca.service.results.MediaDetails;

import java.util.List;

public interface IContentProvider {
    MediaDetails getMediaDetails(int id);

    List<MediaDetailsItem> searchMedia(String query, int page);
}

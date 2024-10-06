package pt.graca.service.external;

import com.pastebin.api.PastebinClient;
import com.pastebin.api.Visibility;
import com.pastebin.api.request.PasteRequest;
import pt.graca.domain.Media;

import java.util.List;

public class PastebinService {

    final PastebinClient client = PastebinClient
            .builder()
            .developerKey(System.getenv("RANKFLIX_PASTEBIN_API_KEY"))
            .build();

    public String createPaste(List<Media> ranking) {
        PasteRequest pasteRequest = PasteRequest
                .content(ranking.stream()
                        .map(media -> ranking.indexOf(media) + 1 + " - " + media.title)
                        .reduce("", (a, b) -> a + b + "\n"))
                .visibility(Visibility.UNLISTED)
                .name("Rankflix Ranking")
                .build();

        return client.paste(pasteRequest);
    }
}

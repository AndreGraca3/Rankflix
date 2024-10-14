package pt.graca.infra.generator.core;

import com.pastebin.api.PastebinClient;
import com.pastebin.api.Visibility;
import com.pastebin.api.request.PasteRequest;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.rank.RankedMedia;
import pt.graca.infra.generator.RankGenerator;

import java.util.List;

public class PastebinGenerator extends RankGenerator {

    final PastebinClient client = PastebinClient
            .builder()
            .developerKey(System.getenv("RANKFLIX_PASTEBIN_API_KEY"))
            .build();

    public String generateRankUrl(RankedMedia ranking, String title) {
        PasteRequest pasteRequest = PasteRequest
                .content(generateTextContent(ranking.rankedMedia()))
                .visibility(Visibility.UNLISTED)
                .name(title)
                .build();

        return client.paste(pasteRequest);
    }
}

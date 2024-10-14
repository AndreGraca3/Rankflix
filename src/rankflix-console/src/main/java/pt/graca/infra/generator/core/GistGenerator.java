package pt.graca.infra.generator.core;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.rank.RankedMedia;
import pt.graca.infra.generator.RankGenerator;

import java.io.IOException;
import java.util.List;

public class GistGenerator extends RankGenerator {

    private final GitHub gitHub;

    {
        try {
            gitHub = new GitHubBuilder()
                    .withOAuthToken(System.getenv("RANKFLIX_GITHUB_TOKEN"))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateRankUrl(RankedMedia ranking, String title) {
        try {
            return gitHub
                    .createGist()
                    .public_(false)
                    .file(title + ".html", generateTextContent(ranking.rankedMedia()))
                    .create()
                    .getHtmlUrl()
                    .toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

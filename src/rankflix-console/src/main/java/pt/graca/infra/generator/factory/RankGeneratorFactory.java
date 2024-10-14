package pt.graca.infra.generator.factory;

import pt.graca.infra.generator.core.ChartGenerator;
import pt.graca.infra.generator.RankGenerator;
import pt.graca.infra.generator.core.GistGenerator;
import pt.graca.infra.generator.core.PastebinGenerator;
import pt.graca.infra.gson.GsonSingleton;

public class RankGeneratorFactory {
    private ChartGenerator chartGenerator;
    private PastebinGenerator pastebinGenerator;
    private GistGenerator gistGenerator;

    public RankGenerator getRankGenerator(RankGeneratorType type) {
        return switch (type) {
            case CHART -> {
                if (chartGenerator == null) {
                    chartGenerator = new ChartGenerator(GsonSingleton.getInstance());
                }
                yield chartGenerator;
            }
            case PASTEBIN -> {
                if (pastebinGenerator == null) {
                    pastebinGenerator = new PastebinGenerator();
                }
                yield pastebinGenerator;
            }
            case GIST -> {
                if (gistGenerator == null) {
                    gistGenerator = new GistGenerator();
                }
                yield gistGenerator;
            }
        };
    }
}


package pt.graca.infra.generator.factory;

import pt.graca.infra.exporters.ExcelExporter;
import pt.graca.infra.generator.RankGenerator;
import pt.graca.infra.generator.core.CanvasGenerator;
import pt.graca.infra.generator.core.ChartGenerator;
import pt.graca.infra.generator.core.GistGenerator;
import pt.graca.infra.gson.GsonSingleton;

public class RankGeneratorFactory {
    private ExcelExporter excelExporter;
    private CanvasGenerator canvasGenerator;
    private ChartGenerator chartGenerator;
    private GistGenerator gistGenerator;

    public RankGenerator getRankGenerator(RankGeneratorType type) {
        return switch (type) {
            case CANVAS -> {
                if (canvasGenerator == null) {
                    canvasGenerator = new CanvasGenerator();
                }
                yield canvasGenerator;
            }
            case CHART -> {
                if (chartGenerator == null) {
                    chartGenerator = new ChartGenerator(GsonSingleton.getInstance());
                }
                yield chartGenerator;
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


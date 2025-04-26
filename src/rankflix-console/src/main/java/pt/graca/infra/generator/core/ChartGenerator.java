package pt.graca.infra.generator.core;

import com.google.gson.Gson;
import io.quickchart.QuickChart;
import pt.graca.api.domain.rank.RankedMedia;
import pt.graca.api.domain.rank.RatedMedia;
import pt.graca.infra.generator.RankGenerator;

import java.util.ArrayList;
import java.util.List;

public class ChartGenerator extends RankGenerator {

    public ChartGenerator(Gson gson) {
        this.gson = gson;
    }

    private final Gson gson;

    public String generateRankUrl(RankedMedia media, String title) {
        QuickChart chart = new QuickChart();

        List<String> titles = new ArrayList<>();
        List<Float> ratings = new ArrayList<>();
        for (int i = 0; i < media.media().size(); i++) {
            RatedMedia m = media.media().get(i);
            titles.add(i + 1 + "º " + m.title());
            ratings.add(m.rating());
        }

        ChartConfig config = new ChartConfig(
                "horizontalBar",
                new Data(titles, List.of(new Dataset(ratings)))
        );

        chart.setBackgroundColor("white");
        chart.setConfig(gson.toJson(config));
        return chart.getShortUrl();
    }
}

record ChartConfig(String type, Data data, ChartOptions options) {
    public ChartConfig(String type, Data data) {
        this(type, data, new ChartOptions());
    }
}

record Data(List<String> labels, List<Dataset> datasets) {
}

record Dataset(List<Float> data, String backgroundColor) {
    public Dataset(List<Float> data) {
        this(data, "orange");
    }
}

record ChartOptions(ScaleOptions scales, LegendOptions legend, TitleOptions title) {
    public ChartOptions() {
        this(new ScaleOptions(), new LegendOptions(), new TitleOptions());
    }
}

record ScaleOptions(List<Axis> xAxes, List<Axis> yAxes) {
    public ScaleOptions() {
        this(List.of(new Axis(
                new Ticks(0, 10F)
        )), List.of(new Axis(new Ticks(0, null))));
    }
}

record LegendOptions(boolean display) {
    public LegendOptions() {
        this(false);
    }
}

record TitleOptions(boolean display) {
    public TitleOptions() {
        this(false);
    }
}

record Axis(Ticks ticks) {
}

record Ticks(float min, Float max) {
    public Ticks(float min, Float max, float stepSize) {
        this(min, max);
    }
}

package pt.graca.service.external;

import com.google.gson.Gson;
import io.quickchart.QuickChart;
import pt.graca.domain.Media;

import java.util.ArrayList;
import java.util.List;

public class ChartService {

    public ChartService(Gson gson) {
        this.gson = gson;
    }

    private final Gson gson;

    public String generateRankingChart(List<Media> media) {
        QuickChart chart = new QuickChart();

        List<String> titles = new ArrayList<>();
        List<Float> ratings = new ArrayList<>();
        for (int i = 0; i < media.size(); i++) {
            Media m = media.get(i);
            titles.add(i + 1 + "º " + m.title);
            ratings.add(m.getRating());
        }

        ChartConfig config = new ChartConfig(
                "horizontalBar",
                new Data(titles, List.of(new Dataset(ratings)))
        );

        chart.setBackgroundColor("white");
        chart.setWidth(1920);
        chart.setHeight(1080);
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
                new Ticks(0, 10F, 0)
        )), List.of(new Axis(new Ticks(0, null, 1))));
    }
}

record LegendOptions(boolean display) {
    public LegendOptions() {
        this(false);
    }
}

record TitleOptions(String text, boolean display) {
    public TitleOptions() {
        this("Ranking", true);
    }
}

record Axis(Ticks ticks) {
}

record Ticks(float min, Float max, float stepSize, boolean autoSkip) {
    public Ticks(float min, Float max, float stepSize) {
        this(min, max, stepSize, false);
    }
}

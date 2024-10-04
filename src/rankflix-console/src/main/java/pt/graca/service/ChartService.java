package pt.graca.service;

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
        media.forEach(m -> {
            titles.add(m.title);
            ratings.add(m.getRating());
        });

        ChartConfig config = new ChartConfig(
                "horizontalBar",
                new Data(
                        titles,
                        List.of(new Dataset(ratings))
                )
        );

        chart.setBackgroundColor("white");
        chart.setConfig(gson.toJson(config));
        return chart.getUrl();
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

record ScaleOptions(List<Axis> xAxes) {
    public ScaleOptions() {
        this(List.of(new Axis()));
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
    public Axis() {
        this(new Ticks());
    }
}

record Ticks(float min, float max) {
    public Ticks() {
        this(0, 10);
    }
}

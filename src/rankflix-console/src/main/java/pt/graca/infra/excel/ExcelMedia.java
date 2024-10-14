package pt.graca.infra.excel;

import java.util.List;

public record ExcelMedia(int tmdbId, String title, List<ExcelRating> ratings) {
}

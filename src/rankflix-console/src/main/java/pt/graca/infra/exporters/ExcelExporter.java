package pt.graca.infra.exporters;

import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.user.User;
import pt.graca.infra.excel.ExcelService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class ExcelExporter implements FileExporter {
    @Override
    public void exportList(List<Media> media, List<User> users, Consumer<File> action) {
        try (var workbook = ExcelService.generateWorkbook(media, users)) {
            var prefix = "rankflix-" + System.currentTimeMillis();
            var suffix = ".xlsx";
            var file = File.createTempFile(prefix, suffix);

            try (var outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }

            action.accept(file);
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package pt.graca.infra.exporters;

import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.user.User;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public interface FileExporter { // TODO: try and merge this into rank generator or something
    void exportList(List<Media> media, List<User> users, Consumer<File> action);
}

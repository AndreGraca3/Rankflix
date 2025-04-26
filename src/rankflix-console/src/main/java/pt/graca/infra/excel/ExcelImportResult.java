package pt.graca.infra.excel;

import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.user.User;

import java.util.List;

public record ExcelImportResult(List<Media> importedMedia, List<User> importedUsers) {
}

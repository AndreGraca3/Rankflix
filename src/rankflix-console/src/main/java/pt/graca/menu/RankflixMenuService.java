package pt.graca.menu;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pt.graca.api.domain.user.User;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.exceptions.RankflixException;
import pt.graca.infra.excel.ExcelImportResult;
import pt.graca.infra.excel.ExcelService;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

public class RankflixMenuService extends ConsoleMenu {

    public RankflixMenuService(Scanner scanner, RankflixService service) {
        super(scanner);

        this.service = service;
    }

    private final RankflixService service;

    @ConsoleMenuOption("Create a new user")
    public void createUser() throws Exception {
        String username = read("Enter the username:");
        service.createUser(username);
    }

    @ConsoleMenuOption("List all users")
    public void listUsers() throws Exception {
        System.out.println("Id | Username | Discord Id");
        service.getAllUsers(null).forEach(user -> System.out.println(user.id + " | " + user.username + " | " + user.discordId));
    }

    @ConsoleMenuOption("Update user")
    public void updateUser() throws Exception {
        String userId = read("Enter the user's internal id:");
        User user = service.findUserById(UUID.fromString(userId));
        if (user == null) throw new NoSuchElementException("User not found");

        String newUsername = read("Enter the new username (leave blank to keep the same):");
        if (newUsername.isBlank()) newUsername = null;
        String newDiscordId = read("Enter the new discord ID (leave blank to keep the same):");
        if (newDiscordId.isBlank()) newDiscordId = null;
        service.updateUser(user.id, newUsername, newDiscordId);
    }

    @ConsoleMenuOption("Add review")
    public void addReview() throws Exception {
        String username = read("Enter the username: ");
        User user = service.findUserByUsername(username);

        String mediaId = read("Enter the media's internal id: ");

        String rating = read("Enter the averageRating: ");
        service.upsertReview(user.id, mediaId, Float.parseFloat(rating), null);
    }

    @ConsoleMenuOption("Delete review (bypass time limit)")
    public void deleteReview() throws Exception {
        String username = read("Enter the username: ");
        User user = service.findUserByUsername(username);
        if (user == null) throw new NoSuchElementException("User not found");

        String mediaId = read("Enter the media's internal id: ");

        service.deleteReviewAdmin(mediaId, user.id);
    }

    @ConsoleMenuOption("Import from Excel (resets list)")
    public void importFromExcel() throws Exception {
        String path = read("Enter the path to the Excel file: ");

        FileInputStream file = new FileInputStream(path.replace("\"", ""));
        Workbook workbook = new XSSFWorkbook(file);

        ExcelImportResult importedExcelRes = ExcelService.importMediaFromWorkbook(workbook);

        System.out.println("Deleting list...");
        service.clearList();

        System.out.println("Importing, this may take a while...");
        service.importMediasWithWatchersRange(importedExcelRes.importedMedia(), importedExcelRes.importedUsers());
    }

    @ConsoleMenuOption("Export to Excel")
    public void exportToExcel() throws Exception {
        String path = read("Enter the path to the Excel file: ");

        var media = service.getAllMedia(null, null);
        var users = service.getAllUsers(media.stream().map(m -> m.watchers).flatMap(Collection::stream).map(w -> w.userId).distinct().toList());

        System.out.println("Exporting...");
        try (var workbook = ExcelService.generateWorkbook(media, users)) {
            var fileName = "rankflix-".concat(String.valueOf(System.currentTimeMillis())).concat(".xlsx");
            ExcelService.saveFile(workbook, path.concat(File.separator).concat(fileName).replace("\"", ""));
        }
    }

    @ConsoleMenuOption("Reset list")
    public void clearList() throws RankflixException {
        service.clearList();
    }

    @ConsoleMenuOption("Delete List")
    public void deleteList() throws RankflixException {
        service.deleteList();
        System.exit(0);
    }
}

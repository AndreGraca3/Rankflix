package pt.graca.menu;

import pt.graca.api.domain.Review;
import pt.graca.api.domain.media.MediaWatcher;
import pt.graca.api.domain.user.User;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.exceptions.RankflixException;
import pt.graca.api.service.exceptions.review.InvalidRatingException;
import pt.graca.infra.excel.ExcelMedia;
import pt.graca.infra.excel.ExcelRating;
import pt.graca.infra.excel.ExcelService;

import java.util.*;

public class RankflixMenuService extends ConsoleMenu {

    public RankflixMenuService(Scanner scanner, RankflixService service) {
        super(scanner);

        this.service = service;
    }

    private final RankflixService service;

    @ConsoleMenuOption("Create a new user")
    public void createUser() throws Exception {
        String username = read("Enter the username:");
        service.createUser(username, null);
    }

    @ConsoleMenuOption("List all users")
    public void listUsers() throws Exception {
        System.out.println("Id | Username | Discord Id");
        service.getAllUsers().forEach(user -> System.out.println(user.id + " | " + user.username + " | " + user.discordId));
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

        String mediaTmdbId = read("Enter the media's TMDB id: ");

        String rating = read("Enter the averageRating: ");
        try {
            service.addReview(user.id, Integer.parseInt(mediaTmdbId), Float.parseFloat(rating), null);
        } catch (NumberFormatException e) {
            throw new InvalidRatingException(rating);
        }
    }

    @ConsoleMenuOption("Delete review (bypass time limit)")
    public void deleteReview() throws Exception {
        String username = read("Enter the username: ");
        User user = service.findUserByUsername(username);
        if (user == null) throw new NoSuchElementException("User not found");

        int mediaTmdbId = Integer.parseInt(read("Enter the media's TMDB id: "));

        service.forceDeleteReview(mediaTmdbId, user.id);
    }

    @ConsoleMenuOption("Import from Excel (overrides list, keeps users)")
    public void importFromExcel() throws Exception {
        String path = read("Enter the path to the Excel file: ");

        List<ExcelMedia> importedMedia = ExcelService.importMedia(path.replace("\"", ""));
        service.clearList();

        Map<String, User> usersMap = new HashMap<>();
        for (ExcelMedia media : importedMedia) {
            System.out.println("Adding media: " + media.title());

            List<MediaWatcher> watchers = new ArrayList<>();

            for (ExcelRating rating : media.ratings()) {
                var user = usersMap.computeIfAbsent(rating.user().username(), k -> {
                    try {
                        var userFromDb = service.findUserByUsername(rating.user().username());
                        if (userFromDb != null) {
                            return userFromDb;
                        }
                        System.out.println("Adding new user: " + rating.user().username());
                        return service.createDiscordUser(rating.user().discordId(), rating.user().username());
                    } catch (RankflixException e) {
                        throw new RuntimeException(e);
                    }
                });

                watchers.add(new MediaWatcher(user.id, new Review(rating.rating(), null)));
            }

            service.addMediaWithWatchers(media.tmdbId(), watchers);
        }
    }

    @ConsoleMenuOption("Delete everything")
    public void deleteEverything() throws RankflixException {
        service.clearList();
        service.deleteAllUsers();
    }
}

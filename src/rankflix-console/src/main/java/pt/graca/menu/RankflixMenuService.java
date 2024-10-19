package pt.graca.menu;

import pt.graca.api.domain.Review;
import pt.graca.api.domain.media.MediaWatcher;
import pt.graca.api.domain.user.User;
import pt.graca.api.service.RankflixService;
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

    @ConsoleMenuOption("Update user's discord ID")
    public void updateUserDiscordId() throws Exception {
        String username = read("Enter the username:");
        User user = service.findUserByUsername(username);
        if (user == null) throw new NoSuchElementException("User not found");

        String newDiscordId = read("Enter the new discord ID:");
        service.updateUserDiscordId(user.id, newDiscordId);
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

    @ConsoleMenuOption("Import from Excel (overrides everything)")
    public void importFromExcel() throws Exception {
        String path = read("Enter the path to the Excel file: ");

        List<ExcelMedia> importedMedia = ExcelService.importMedia(path.replace("\"", ""));
        service.clearAll();

        Map<String, User> usersMap = new HashMap<>();
        for (ExcelMedia media : importedMedia) {
            System.out.println("Adding media: " + media.title());

            List<MediaWatcher> watchers = new ArrayList<>();

            for (ExcelRating rating : media.ratings()) {
                var user = usersMap.computeIfAbsent(rating.user().username(), k -> {
                    try {
                        System.out.println("Adding user: " + rating.user().username());
                        return service.createDiscordUser(rating.user().discordId(), rating.user().username());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                watchers.add(new MediaWatcher(user.id, new Review(rating.rating(), null)));
            }

            service.addMediaWithWatchers(media.tmdbId(), watchers);
        }
    }
}

package pt.graca.menu;

import pt.graca.api.domain.Media;
import pt.graca.api.domain.User;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.exceptions.review.InvalidRatingException;
import pt.graca.discord.DiscordWebhookService;
import pt.graca.infra.generator.IRankGenerator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class RankflixMenuService extends ConsoleMenu {

    public RankflixMenuService(Scanner scanner, RankflixService service, IRankGenerator rankGenerator) {
        super(scanner);

        this.service = service;
        this.rankGenerator = rankGenerator;
    }

    private final RankflixService service;
    private final IRankGenerator rankGenerator;
    private final DiscordWebhookService discordWebhookService
            = new DiscordWebhookService(System.getenv("RANKFLIX_DISCORD_WEBHOOK_URL"));

    @ConsoleMenuOption("Create a new user")
    public void createUser() throws Exception {
        String username = read("Enter the username:");
        service.createUser(username, null);
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

    @ConsoleMenuOption("Delete review (only for after 5 minutes)")
    public void deleteReview() throws Exception {
        String username = read("Enter the username: ");
        User user = service.findUserByUsername(username);
        if (user == null) throw new NoSuchElementException("User not found");

        int mediaTmdbId = Integer.parseInt(read("Enter the media's TMDB id: "));

        service.deleteRating(mediaTmdbId, user.id);
    }

    @ConsoleMenuOption("Send ranking to Discord webhook")
    public void sendToDiscord() throws Exception {
        List<Media> media = service.getTopRankedMedia(null);

        var generatedRankUrl = rankGenerator.generateRankUrl(media);
        discordWebhookService.sendMessage(generatedRankUrl);
    }
}

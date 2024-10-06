package pt.graca.menu;

import pt.graca.domain.Media;
import pt.graca.domain.User;
import pt.graca.service.RankflixService;
import pt.graca.service.exceptions.InvalidRatingException;
import pt.graca.service.exceptions.MediaNotFoundException;
import pt.graca.service.external.discord.DiscordWebhookService;
import pt.graca.service.external.PastebinService;

import java.util.List;
import java.util.Scanner;

public class RankflixMenuService extends Menu {

    public RankflixMenuService(Scanner scanner, RankflixService service, PastebinService pastebinService, DiscordWebhookService discordWebhookService) {
        super(scanner);

        this.service = service;
        this.pastebinService = pastebinService;
        this.discordWebhookService = discordWebhookService;
    }

    private final RankflixService service;
    private final PastebinService pastebinService;
    private final DiscordWebhookService discordWebhookService;

    @MenuOption("Create a new user")
    public void createUser() throws Exception {
        String username = read("Enter the username:");
        service.createUser(username);
    }

    @MenuOption("Add rating")
    public void addRating() throws Exception {
        String username = read("Enter the username: ");
        User user = service.findUserByUsername(username);

        String mediaId = read("Enter the media's TMDB id: ");

        String rating = read("Enter the rating: ");
        try {
            service.addRating(user, mediaId, Float.parseFloat(rating));
        } catch (NumberFormatException e) {
            throw new InvalidRatingException(rating);
        }
    }

    @MenuOption("Delete rating (only for after 5 minutes)")
    public void deleteRating() throws Exception {
        String username = read("Enter the username: ");
        User user = service.findUserByUsername(username);

        String mediaTmdbId = read("Enter the media's TMDB id: ");
        service.findMediaByTmdbId(mediaTmdbId);

        service.deleteRating(mediaTmdbId, user.userId);
    }

    @MenuOption("Send ranking to Discord channel")
    public void sendToDiscord() throws Exception {
        List<Media> media = service.getAllMedia();

        // make a ranking based on the average rating
        media.sort((o1, o2) ->
                Float.compare(o2.ratingSum / o2.ratings.size(), o1.ratingSum / o1.ratings.size()));

        // var chartUrl = chartService.generateRankingChart(media);
        var pasteUrl = pastebinService.createPaste(media);

        // send to discord
        discordWebhookService.sendMessage(pasteUrl);
    }
}

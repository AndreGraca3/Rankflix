package pt.graca.menu;

import pt.graca.domain.Media;
import pt.graca.domain.User;
import pt.graca.exceptions.RankflixException;
import pt.graca.service.ChartService;
import pt.graca.service.DiscordWebhookService;
import pt.graca.service.RankflixService;

import java.lang.reflect.Method;
import java.util.*;

public class MenuService {

    public MenuService(RankflixService service, ChartService chartService, DiscordWebhookService discordWebhookService) {
        this.service = service;
        this.chartService = chartService;
        this.discordWebhookService = discordWebhookService;

        Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(MenuOption.class))
                .sorted(Comparator.comparing(Method::getName))
                .forEach(method -> menuOptions.put(menuOptions.size() + 1, method));
    }

    private final RankflixService service;
    private final ChartService chartService;
    private final DiscordWebhookService discordWebhookService;

    private final Scanner scanner = new Scanner(System.in);
    private final Map<Integer, Method> menuOptions = new HashMap<>();

    public void printMenuForever() {
        while (true) {
            System.out.println("Choose an option:");
            menuOptions.forEach((key, value) -> System.out.println(key + " - " + value.getAnnotation(MenuOption.class).value()));
            int option = Integer.parseInt(scanner.nextLine());
            Method method = menuOptions.get(option);
            try {
                method.invoke(this);
                System.out.println("😊 Operation completed successfully. Press enter to continue...");
            } catch (Exception e) {
                System.out.println("❌ An error occurred: " + e.getCause().getMessage());
            } finally {
                scanner.nextLine();
                System.out.println("-".repeat(50));
            }
        }
    }

    @MenuOption("Create a new user")
    public void createUser() throws Exception {
        String username = read("Enter the username:");
        service.createUser(username);
    }

    @MenuOption("Add rating")
    public void addRating() throws Exception {
        String mediaId = read("Enter the media's IMDB id: ");

        String username = read("Enter the username: ");
        User user = service.findUserOrThrow(username);

        String rating = read("Enter the rating: ");
        try {
            service.addRating(user, mediaId, Float.parseFloat(rating), () -> read("Enter the media's title: "));
        } catch (NumberFormatException e) {
            throw new RankflixException.InvalidRatingException(rating);
        }
    }

    @MenuOption("Add multiple ratings")
    public void addMultipleRatings() throws Exception {
        String mediaId = read("Enter the media's IMDB id: ");

        // Get users ratings
        Map<String, Float> ratings = new HashMap<>();
        while (true) {
            String userRating = read("Enter the username and rating (username:rating): ");
            if (userRating.isBlank()) {
                break;
            }

            String[] parts = userRating.split(":");
            if (parts.length != 2) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            String username = parts[0];
            try {
                float rating = Float.parseFloat(parts[1]);
                ratings.put(username, rating);
            } catch (NumberFormatException e) {
                System.out.println("Invalid rating. Please try again.");
            }

            System.out.println("Rating of user \"" + username + "\" added.");
        }

        service.addMultipleRatings(mediaId, ratings, () -> read("Enter the media's title: "));
    }

    @MenuOption("Delete rating (only for after 5 minutes)")
    public void deleteRating() throws Exception {
        String username = read("Enter the username: ");
        service.findUserOrThrow(username);

        String mediaId = read("Enter the media's IMDB id: ");
        Media media = service.findMediaOrThrow(mediaId);

        service.deleteRating(media, username);
    }

    @MenuOption("Send ranking to Discord channel")
    public void sendToDiscord() throws Exception {
        List<Media> media = service.getAllMedia();

        // make a ranking based on the average rating
        media.sort(new Comparator<Media>() {
            @Override
            public int compare(Media o1, Media o2) {
                return Float.compare(o2.ratingSum / o2.ratings.size(), o1.ratingSum / o1.ratings.size());
            }
        });

        // generate ranking image
        var chartUrl = chartService.generateRankingChart(media);

        // send to discord
        discordWebhookService.sendMessage(chartUrl);
    }

    private String read(String s) {
        System.out.print(s);
        return scanner.nextLine();
    }
}

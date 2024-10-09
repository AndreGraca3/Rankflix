package pt.graca.repo.file;

import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;
import pt.graca.domain.Media;
import pt.graca.domain.RankflixList;
import pt.graca.domain.Rating;
import pt.graca.domain.User;
import pt.graca.repo.IRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

public class FileRepository implements IRepository {

    public FileRepository(Gson gson, String folderName, String listName) throws IOException {
        this.gson = gson;
        this.fileLocation = folderName.concat(File.separator).concat(listName).concat(".json");

        File file = new File(fileLocation);
        if (!file.exists() || file.length() == 0) {
            rankflixList = new RankflixList(listName);
            Files.write(file.toPath(), gson.toJson(rankflixList).getBytes());
            return;
        }

        rankflixList = gson.fromJson(Files.readString(file.toPath()), RankflixList.class);
        System.out.println("Loaded data created at " + rankflixList.creationDate);
    }

    private final String fileLocation;
    private final Gson gson;
    private final RankflixList rankflixList;

    @Override
    public String getListName() {
        return rankflixList.listName;
    }

    public void insertUser(User user) {
        rankflixList.users.add(user);
    }

    @Override
    public User findUserByUsername(String username) {
        for (User user : rankflixList.users) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User findUserById(UUID userId) {
        for (User user : rankflixList.users) {
            if (user.id.equals(userId)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User findUserByDiscordId(String discordId) {
        for (User user : rankflixList.users) {
            if (user.discordId.equals(discordId)) {
                return user;
            }
        }
        return null;
    }

    public void insertMedia(Media media) {
        rankflixList.media.add(media);
    }

    public List<Media> getAllMedia(@Nullable String query) {
        return rankflixList.media.stream()
                .filter(media -> query == null || media.title.toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public Media findMediaByTmdbId(int mediaTmdbId) {
        for (Media media : rankflixList.media) {
            if (media.tmdbId == mediaTmdbId) {
                return media;
            }
        }
        return null;
    }

    @Override
    public void updateMedia(Media media) {
        for (int i = 0; i < rankflixList.media.size(); i++) {
            if (rankflixList.media.get(i).tmdbId == media.tmdbId) {
                rankflixList.media.set(i, media);
                return;
            }
        }
    }

    @Override
    public void deleteMedia(Media media) {
        rankflixList.media.remove(media);
    }

    @Override
    public Rating findRating(int mediaTmdbId, UUID userId) {
        var media = findMediaByTmdbId(mediaTmdbId);
        if (media == null) return null;

        for (Rating rating : media.ratings) {
            if (rating.userId.equals(userId)) {
                return rating;
            }
        }

        return null;
    }

    public void saveData() throws IOException {
        String json = gson.toJson(rankflixList);
        Files.write(new File(fileLocation).toPath(), json.getBytes());
    }
}

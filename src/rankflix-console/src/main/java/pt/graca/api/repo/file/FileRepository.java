package pt.graca.api.repo.file;

import com.google.gson.Gson;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.media.MediaWatcher;
import pt.graca.api.domain.user.User;
import pt.graca.api.repo.IRepository;
import pt.graca.api.repo.file.dto.RankflixList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

public class FileRepository implements IRepository {

    public FileRepository(Gson gson, String folderName, String listName) throws IOException {
        if (listName.isBlank()) {
            throw new IllegalArgumentException("List name cannot be blank");
        }

        this.gson = gson;
        this.fileLocation = folderName.concat(File.separator).concat(listName).concat(".json");

        File file = new File(fileLocation);
        if (!file.exists() || file.length() == 0) {
            rankflixList = new RankflixList(listName);
            Files.write(file.toPath(), gson.toJson(rankflixList).getBytes());
            return;
        }

        rankflixList = gson.fromJson(Files.readString(file.toPath()), RankflixList.class);
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
    public void updateUser(User user) {
        for (int i = 0; i < rankflixList.users.size(); i++) {
            if (rankflixList.users.get(i).id.equals(user.id)) {
                rankflixList.users.set(i, user);
                return;
            }
        }
    }

    @Override
    public List<User> getAllUsers() {
        return rankflixList.users;
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

    @Override
    public void deleteAllUsers() {
        rankflixList.users.clear();
    }

    public void insertMedia(Media media) {
        rankflixList.media.add(media);
    }

    @Override
    public List<Media> getAllSortedMedia(@Nullable String query, @Nullable UUID userId, @Nullable Integer limit) {
        return rankflixList.media.stream()
                .filter(media -> query == null || media.title.toLowerCase().contains(query.toLowerCase()))
                .filter(media -> userId == null || media.isWatchedBy(userId))
                .limit(limit == null ? rankflixList.media.size() : limit)
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
    public MediaWatcher findWatcher(UUID userId, int mediaTmdbId) {
        var media = findMediaByTmdbId(mediaTmdbId);
        if (media == null) return null;

        return media.getWatcher(userId);
    }

    @Override
    public void clearList() {
        rankflixList.media.clear();
        rankflixList.users.clear();
    }

    // transaction methods, doest work with concurrent transactions, so just don't use this repo
    public void saveData() {
        String json = gson.toJson(rankflixList);
        try {
            Files.write(new File(fileLocation).toPath(), json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

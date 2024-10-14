package pt.graca.api.repo.mongo;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.Review;
import pt.graca.api.domain.media.MediaWatcher;
import pt.graca.api.domain.user.User;
import pt.graca.api.repo.IRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MongoRepository implements IRepository {

    public MongoRepository(MongoDatabase database, ClientSession session, String listName) {
        this.database = database;
        this.session = session;
        this.listName = listName;

        MongoCollection<Document> listsCollection = database.getCollection("lists");

        listsCollection.createIndex(new Document("name", 1), new IndexOptions().unique(true));
        listsCollection.createIndex(new Document("media.tmdbId", 1), new IndexOptions().unique(true).sparse(true));
        listsCollection.createIndex(new Document("media.ratingSum", 1), new IndexOptions().sparse(true));
        listsCollection.createIndex(new Document("media.watchers.userId", 1), new IndexOptions().sparse(true));

        if (listsCollection.find(new Document("name", listName)).first() != null) return;

        listsCollection.insertOne(new Document()
                .append("name", listName)
                .append("media", List.of())
        );
    }

    private final MongoDatabase database;
    private final ClientSession session;
    private final String listName;

    @Override
    public String getListName() {
        return listName;
    }

    @Override
    public void insertUser(User user) {
        database.getCollection("users")
                .insertOne(session, new Document()
                        .append("username", user.username)
                        .append("_id", user.id.toString())
                        .append("discordId", user.discordId)
                        .append("avatarUrl", user.avatarUrl)
                );
    }

    @Override
    public User findUserByUsername(String username) {
        return database.getCollection("users")
                .find(session, new Document("username", username))
                .map(document -> new User(
                        UUID.fromString(document.getString("_id")),
                        document.getString("discordId"),
                        document.getString("username"),
                        document.getString("avatarUrl")
                ))
                .first();
    }

    @Override
    public User findUserById(UUID userId) {
        return database.getCollection("users")
                .find(session, new Document("_id", userId.toString()))
                .map(document -> new User(
                        UUID.fromString(document.getString("_id")),
                        document.getString("discordId"),
                        document.getString("username"),
                        document.getString("avatarUrl")
                ))
                .first();
    }

    @Override
    public User findUserByDiscordId(String discordId) {
        return database.getCollection("users")
                .find(session, new Document("discordId", discordId))
                .map(document -> new User(
                        UUID.fromString(document.getString("_id")),
                        document.getString("discordId"),
                        document.getString("username"),
                        document.getString("avatarUrl")
                ))
                .first();
    }

    @Override
    public void insertMedia(Media media) {
        database.getCollection("lists")
                .updateOne(session, new Document("name", listName),
                        new Document("$push", new Document("media", new Document()
                                .append("tmdbId", media.tmdbId)
                                .append("title", media.title)
                                .append("ratingSum", media.ratingSum)
                                .append("watchers", media.watchers.stream()
                                        .map(this::mapWatcherToDocument).toList())
                        ))
                );
    }

    @Override
    public List<Media> getAllSortedMedia(@Nullable String query, @Nullable UUID userId) {
        return Objects.requireNonNull(database.getCollection("lists")
                        .find(session, new Document("name", listName))
                        .map(document -> document.getList("media", Document.class))
                        .first())
                .stream()
                .filter(doc -> query == null ||
                        doc.getString("title").toLowerCase().contains(query.toLowerCase())
                )
                .filter(doc -> userId == null ||
                        doc.getList("watchers", Document.class).stream()
                                .anyMatch(watcherDoc -> watcherDoc.getString("userId").equals(userId.toString()))
                )
                .map(this::mapDocumentToMedia)
                .toList();
    }

    @Override
    public Media findMediaByTmdbId(int mediaTmdbId) {
        return Objects.requireNonNull(database.getCollection("lists")
                        .find(session, new Document("name", listName))
                        .map(document -> document.getList("media", Document.class))
                        .first())
                .stream()
                .filter(doc -> doc.getInteger("tmdbId") == mediaTmdbId)
                .map(this::mapDocumentToMedia)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void updateMedia(Media media) {
        database.getCollection("lists")
                .updateOne(session, new Document("name", listName)
                                .append("media.tmdbId", media.tmdbId),
                        new Document("$set", new Document()
                                .append("media.$.ratingSum", media.ratingSum)
                                .append("media.$.watchers", media.watchers.stream()
                                        .map(this::mapWatcherToDocument).toList()
                                )
                        )
                );
    }

    @Override
    public void deleteMedia(Media media) {
        database.getCollection("lists")
                .updateOne(session, new Document("name", listName),
                        new Document("$pull", new Document("media", new Document("tmdbId", media.tmdbId)))
                );
    }

    @Override
    public MediaWatcher findWatcher(UUID userId, int mediaTmdbId) {
        return Objects.requireNonNull(database.getCollection("lists")
                        .find(session, new Document("name", listName))
                        .map(document -> document.getList("media", Document.class))
                        .first())
                .stream()
                .filter(doc -> doc.getInteger("tmdbId") == mediaTmdbId)
                .flatMap(doc -> doc.getList("watchers", Document.class).stream())
                .filter(watcherDoc -> watcherDoc.getString("userId").equals(userId.toString()))
                .map(this::mapDocumentToWatcher)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void clearAll() {
        database.getCollection("users").deleteMany(session, new Document());

        database.getCollection("lists")
                .updateOne(session, new Document("name", listName),
                        new Document("$set", new Document("media", List.of()))
                );
    }

    // helper methods to map object to a document

    private Document mapWatcherToDocument(MediaWatcher watcher) {
        return new Document()
                .append("userId", watcher.userId.toString())
                .append("review", watcher.review != null ? new Document()
                        .append("value", watcher.review.rating)
                        .append("comment", watcher.review.comment)
                        .append("createdAt", watcher.review.createdAt)
                        : null);
    }

    // helper method to map a document to object

    private Media mapDocumentToMedia(Document doc) {
        return new Media(
                doc.getInteger("tmdbId"),
                doc.getString("title"),
                doc.getDouble("ratingSum").floatValue(),
                doc.getList("watchers", Document.class).stream()
                        .map(watcherDoc -> new MediaWatcher(
                                UUID.fromString(watcherDoc.getString("userId")),
                                mapDocumentToReview(watcherDoc.get("review", Document.class))
                        )).toList()
        );
    }

    private Review mapDocumentToReview(Document doc) {
        return new Review(
                doc.getDouble("value").floatValue(),
                doc.getString("comment"),
                doc.getDate("createdAt").toInstant()
        );
    }

    private MediaWatcher mapDocumentToWatcher(Document doc) {
        return new MediaWatcher(
                UUID.fromString(doc.getString("userId")),
                mapDocumentToReview(doc.get("review", Document.class))
        );
    }
}

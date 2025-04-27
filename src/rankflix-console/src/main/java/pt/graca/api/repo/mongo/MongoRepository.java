package pt.graca.api.repo.mongo;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.Review;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.media.MediaWatcher;
import pt.graca.api.domain.user.User;
import pt.graca.api.repo.IRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoRepository implements IRepository {

    public MongoRepository(MongoDatabase database, ClientSession session, String listName) {
        this.database = database;
        this.session = session;
        this.listName = listName;

        MongoCollection<Document> listsCollection = database.getCollection("lists");

        listsCollection.createIndex(new Document("name", 1), new IndexOptions().unique(true));
        listsCollection.createIndex(new Document("media.tmdbId", 1), new IndexOptions().unique(true).sparse(true));
        listsCollection.createIndex(new Document("media.averageRating", 1), new IndexOptions().sparse(true));
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
                );
    }

    public void insertUserRange(List<User> users) {
        database.getCollection("users")
                .insertMany(session, users.stream()
                        .map(user -> new Document()
                                .append("username", user.username)
                                .append("_id", user.id.toString())
                                .append("discordId", user.discordId)
                        )
                        .toList()
                );
    }

    @Override
    public List<User> getAllUsers(List<UUID> ids) {
        var collection = database.getCollection("users");
        var filter = (ids == null)
                ? new Document()
                : new Document("_id", new Document("$in", ids.stream().map(UUID::toString).toList()));

        return collection.find(session, filter)
                .map(doc -> new User(
                        UUID.fromString(doc.getString("_id")),
                        doc.getString("discordId"),
                        doc.getString("username")
                ))
                .into(new ArrayList<>());
    }

    @Override
    public void updateUser(User user) {
        database.getCollection("users")
                .updateOne(session, new Document("_id", user.id.toString()),
                        new Document("$set", new Document()
                                .append("username", user.username)
                                .append("discordId", user.discordId)
                        )
                );
    }

    @Override
    public User findUserByUsername(String username) {
        return database.getCollection("users")
                .find(session, new Document("username", username))
                .map(document -> new User(
                        UUID.fromString(document.getString("_id")),
                        document.getString("discordId"),
                        document.getString("username")
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
                        document.getString("username")
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
                        document.getString("username")
                ))
                .first();
    }

    @Override
    public void deleteAllUsers() {
        database.getCollection("users")
                .deleteMany(session, new Document());
    }

    @Override
    public void insertMedia(Media media) {
        database.getCollection("lists")
                .updateOne(session, new Document("name", listName),
                        new Document("$push", new Document("media", new Document()
                                .append("tmdbId", media.tmdbId)
                                .append("title", media.title)
                                .append("averageRating", media.averageRating)
                                .append("watchers", media.watchers.stream().map(this::mapWatcherToDocument).toList())
                                .append("createdAt", media.createdAt)
                                .append("isImported", media.isImported)
                        ))
                );
    }

    @Override
    public void insertMediaRange(List<Media> mediaItems) {
        database.getCollection("lists")
                .updateOne(session, new Document("name", listName),
                        new Document("$push", new Document("media",
                                new Document("$each", mediaItems.stream()
                                        .map(media -> new Document()
                                                .append("tmdbId", media.tmdbId)
                                                .append("title", media.title)
                                                .append("averageRating", media.averageRating)
                                                .append("watchers", media.watchers.stream()
                                                        .map(this::mapWatcherToDocument)
                                                        .toList()
                                                )
                                                .append("createdAt", media.createdAt)
                                                .append("isImported", media.isImported)
                                        )
                                        .toList()
                                )
                        ))
                );
    }

    @Override
    public List<Media> getAllSortedMedia(@Nullable String searchQuery, @Nullable UUID userId, @Nullable Integer limit) {
        List<Bson> pipeline = new ArrayList<>();

        // Match the list by name
        pipeline.add(Aggregates.match(Filters.eq("name", listName)));

        // Unwind the media array to process each element
        pipeline.add(Aggregates.unwind("$media"));

        // Apply filter conditions inside the media array
        List<Bson> mediaFilters = new ArrayList<>();

        // Title filter using regex
        if (searchQuery != null && !searchQuery.isBlank()) {
            mediaFilters.add(Filters.regex("media.title", searchQuery, "i"));
        }

        // Watchers filter based on userId and review
        if (userId != null) {
            mediaFilters.add(Filters.elemMatch("media.watchers", Filters.and(
                    Filters.eq("userId", userId.toString()),
                    Filters.exists("review")
            )));
        }

        // Add filter condition for media
        if (!mediaFilters.isEmpty()) {
            pipeline.add(Aggregates.match(Filters.and(mediaFilters)));
        }

        // Sort the media by average rating (descending order)
        pipeline.add(Aggregates.sort(Sorts.descending("averageRating")));

        // Limit the results if a limit is provided
        if (limit != null && limit > 0) {
            pipeline.add(Aggregates.limit(limit));
        }

        // Group back the media into lists after filtering
        pipeline.add(Aggregates.group("$_id", Accumulators.push("media", "$media")));

        // Project the media array and other necessary fields
        pipeline.add(Aggregates.project(Projections.include("media")));

        // Execute the aggregation pipeline
        var mediaDocs = database.getCollection("lists")
                .aggregate(pipeline)
                .map(document -> document.getList("media", Document.class))
                .first();

        return mediaDocs == null ? new ArrayList<>() : mediaDocs
                .stream()
                .map(this::mapDocumentToMedia)
                .toList();
    }

    @Override
    public Media findMediaByTmdbId(int mediaTmdbId) {
        // filter and get media from array on db side using projection
        var projection = Projections.elemMatch("media", Filters.eq("tmdbId", mediaTmdbId));
        var mediaDoc = database.getCollection("lists")
                .find(session, new Document("name", listName))
                .projection(projection)
                .map(document -> document.getList("media", Document.class))
                .first();

        return mediaDoc == null ? null : mapDocumentToMedia(mediaDoc.getFirst());
    }

    @Override
    public void updateMedia(Media media) {
        database.getCollection("lists")
                .updateOne(session, new Document("name", listName)
                                .append("media.tmdbId", media.tmdbId),
                        new Document("$set", new Document()
                                .append("media.$.averageRating", media.averageRating)
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
        List<Bson> filters = new ArrayList<>();
        filters.add(Filters.eq("name", listName));
        filters.add(Filters.eq("media.tmdbId", mediaTmdbId));
        filters.add(Filters.eq("media.watchers.userId", userId.toString()));

        Bson combinedFilter = Filters.and(filters);

        var mediaDocs = database.getCollection("lists")
                .find(session, combinedFilter)
                .map(document -> document.getList("media", Document.class))
                .first();

        return mediaDocs == null ? null : mediaDocs
                .stream()
                .flatMap(doc -> doc.getList("watchers", Document.class).stream())
                .filter(watcherDoc -> watcherDoc.getString("userId").equals(userId.toString()))
                .map(this::mapDocumentToWatcher)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void clearList() {
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
                        .append("rating", String.valueOf(watcher.review.rating))
                        .append("comment", watcher.review.comment)
                        .append("createdAt", watcher.review.createdAt)
                        : null);
    }

    // helper methods to map a document to object

    private Media mapDocumentToMedia(Document doc) {
        return new Media(
                doc.getInteger("tmdbId"),
                doc.getString("title"),
                doc.getDouble("averageRating").floatValue(),
                doc.getList("watchers", Document.class).stream()
                        .map(watcherDoc -> new MediaWatcher(
                                UUID.fromString(watcherDoc.getString("userId")),
                                watcherDoc.get("review", Document.class) == null
                                        ? null
                                        : mapDocumentToReview(watcherDoc.get("review", Document.class))
                        )).toList(),
                doc.getDate("createdAt").toInstant(),
                doc.getBoolean("isImported")
        );
    }

    private Review mapDocumentToReview(Document doc) {
        return new Review(
                Float.parseFloat(doc.getString("rating")),
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

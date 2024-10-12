package pt.graca.api.repo.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import pt.graca.api.domain.Media;
import pt.graca.api.domain.Review;
import pt.graca.api.domain.User;
import pt.graca.api.repo.IRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MongoRepository implements IRepository {

    public MongoRepository(MongoDatabase database, String listName) {
        this.database = database;
        this.listName = listName;

        MongoCollection<Document> listsCollection = database.getCollection("lists");

        listsCollection.createIndex(new Document("name", 1), new IndexOptions().unique(true));
        listsCollection.createIndex(new Document("media.tmdbId", 1), new IndexOptions().unique(true).sparse(true));
        listsCollection.createIndex(new Document("media.ratingSum", 1), new IndexOptions().sparse(true));

        if (listsCollection.find(new Document("name", listName)).first() != null) return;

        listsCollection.insertOne(new Document()
                .append("name", listName)
                .append("media", List.of())
        );
    }

    private final MongoDatabase database;
    private final String listName;

    @Override
    public String getListName() {
        return listName;
    }

    @Override
    public void insertUser(User user) {
        database.getCollection("users")
                .insertOne(new Document()
                        .append("username", user.username)
                        .append("_id", user.id.toString())
                        .append("discordId", user.discordId)
                        .append("avatarUrl", user.avatarUrl)
                );
    }

    @Override
    public User findUserByUsername(String username) {
        return database.getCollection("users")
                .find(new Document("username", username))
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
                .find(new Document("_id", userId.toString()))
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
                .find(new Document("discordId", discordId))
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
                .updateOne(new Document("name", listName),
                        new Document("$push", new Document("media", new Document()
                                .append("tmdbId", media.tmdbId)
                                .append("title", media.title)
                                .append("ratingSum", media.ratingSum)
                                .append("reviews", media.reviews)
                                .append("userIds", media.userIds)
                        ))
                );
    }

    @Override
    public List<Media> getAllSortedMediaByRating(@Nullable String query) {
        return Objects.requireNonNull(database.getCollection("lists")
                        .find(new Document("name", listName))
                        .map(document -> document.getList("media", Document.class))
                        .first())
                .stream()
                .map(this::mapDocumentToMedia)
                .toList();
    }

    @Override
    public Media findMediaByTmdbId(int mediaTmdbId) {
        return Objects.requireNonNull(database.getCollection("lists")
                        .find(new Document("name", listName))
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
                .updateOne(new Document("name", listName)
                                .append("media.tmdbId", media.tmdbId),
                        new Document("$set", new Document()
                                .append("media.$.ratingSum", media.ratingSum)
                                .append("media.$.reviews", media.reviews.stream()
                                        .map(review -> new Document()
                                                .append("userId", review.userId.toString())
                                                .append("value", review.value)
                                                .append("comment", review.comment)
                                                .append("createdAt", review.createdAt)
                                        )
                                        .toList()
                                )
                                .append("media.$.userIds", media.userIds.stream()
                                        .map(UUID::toString)
                                        .toList()
                                )
                        )
                );
    }

    @Override
    public void deleteMedia(Media media) {
        database.getCollection("lists")
                .updateOne(new Document("name", listName),
                        new Document("$pull", new Document("media", new Document("tmdbId", media.tmdbId)))
                );
    }

    @Override
    public Review findReview(int mediaTmdbId, UUID userId) {
        return Objects.requireNonNull(database.getCollection("lists")
                        .find(new Document("name", listName))
                        .map(document -> document.getList("media", Document.class))
                        .first())
                .stream()
                .filter(doc -> doc.getInteger("tmdbId") == mediaTmdbId)
                .map(doc -> doc.getList("reviews", Document.class))
                .flatMap(List::stream)
                .filter(reviewDoc -> reviewDoc.getString("userId").equals(userId.toString()))
                .map(this::mapDocumentToReview)
                .findFirst()
                .orElse(null);
    }

    private Media mapDocumentToMedia(Document doc) {
        return new Media(
                doc.getInteger("tmdbId"),
                doc.getString("title"),
                doc.getDouble("ratingSum").floatValue(),
                doc.getList("reviews", Document.class).stream()
                        .map(reviewDoc -> new Review(
                                UUID.fromString(reviewDoc.getString("userId")),
                                reviewDoc.getDouble("value").floatValue(),
                                reviewDoc.getString("comment"),
                                reviewDoc.getDate("createdAt").toInstant()
                        ))
                        .toList(),
                doc.getList("userIds", String.class).stream()
                        .map(UUID::fromString)
                        .toList()
        );
    }

    private Review mapDocumentToReview(Document doc) {
        return new Review(
                UUID.fromString(doc.getString("userId")),
                doc.getDouble("value").floatValue(),
                doc.getString("comment"),
                doc.getDate("createdAt").toInstant()
        );
    }
}

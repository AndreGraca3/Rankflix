package pt.graca.api.domain.user;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class User {

    public User(UUID id, String discordId, String username) {
        this.id = id;
        this.discordId = discordId;
        this.username = username;
    }

    public User(String discordId, String username) {
        this.id = UUID.randomUUID();
        this.discordId = discordId;
        this.username = username;
    }

    public User(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
    }

    @NotNull
    public UUID id;

    public String discordId;

    public String username;

    public User updateUser(String username, String discordId) {
        return new User(this.id, discordId, username);
    }
}

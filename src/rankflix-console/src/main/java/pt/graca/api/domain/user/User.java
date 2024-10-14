package pt.graca.api.domain.user;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class User {

    public User(UUID id, String discordId, String username, String avatarUrl) {
        this.id = id;
        this.discordId = discordId;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public User(String discordId, String username, String avatarUrl) {
        this.id = UUID.randomUUID();
        this.discordId = discordId;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public User(String username, String avatarUrl) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    @NotNull
    public UUID id;

    public String discordId;

    public String username;

    public String avatarUrl;
}

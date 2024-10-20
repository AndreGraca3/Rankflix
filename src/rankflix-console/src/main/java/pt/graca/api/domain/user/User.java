package pt.graca.api.domain.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public User updateUser(@Nullable String username,@Nullable String discordId) {
        var newUsername = username == null ? this.username : username;
        var newDiscordId = discordId == null ? this.discordId : discordId;
        return new User(this.id, newDiscordId, newUsername);
    }
}

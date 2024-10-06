package pt.graca.domain;

import java.util.UUID;

public class User {

    public User(UUID userId, String discordId, String username) {
        this.userId = userId;
        this.discordId = discordId;
        this.username = username;
    }

    public User(String discordId, String username) {
        this.userId = UUID.randomUUID();
        this.discordId = discordId;
        this.username = username;
    }

    public User(String username) {
        this.userId = UUID.randomUUID();
        this.username = username;
    }

    public UUID userId;
    public String discordId;
    public String username;
}

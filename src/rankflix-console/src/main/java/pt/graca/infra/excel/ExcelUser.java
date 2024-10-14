package pt.graca.infra.excel;

public record ExcelUser(String discordId, String username) {
    public ExcelUser {
        if (discordId == null || discordId.isBlank()) {
            throw new IllegalArgumentException("Discord ID cannot be null or empty");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
    }
}

package pt.graca.api.repo.file.dto;

import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.user.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RankflixList {

    public RankflixList(String listName) {
        this.listName = listName;
    }

    public String listName;

    public List<User> users = new ArrayList<>();

    public List<Media> media = new ArrayList<>();

    public Instant creationDate = Instant.now();
}
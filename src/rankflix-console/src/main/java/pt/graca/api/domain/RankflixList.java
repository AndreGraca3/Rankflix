package pt.graca.api.domain;

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
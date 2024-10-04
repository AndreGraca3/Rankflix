package pt.graca.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RankflixData {
    public List<User> users = new ArrayList<>();

    public List<Media> media = new ArrayList<>();

    public Instant creationDate = Instant.now();
}


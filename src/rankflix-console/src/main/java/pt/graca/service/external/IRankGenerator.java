package pt.graca.service.external;

import pt.graca.domain.Media;

import java.util.List;

public interface IRankGenerator {
    String generateRankUrl(List<Media> ranking);
}

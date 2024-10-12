package pt.graca.infra.generator;

import pt.graca.api.domain.Media;

import java.util.List;

public interface IRankGenerator {
    String generateRankUrl(List<Media> ranking);
}

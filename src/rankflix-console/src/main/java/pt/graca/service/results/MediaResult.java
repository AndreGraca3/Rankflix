package pt.graca.service.results;

import pt.graca.domain.MediaType;

public record MediaResult(String mediaTmdbId, String title, String overview, String posterUrl, MediaType mediaType) {
}

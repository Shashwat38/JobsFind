
package com.jobsfindai.backend.job;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class MunicipalityService {

    private final RestClient restClient;

    private Map<String, String> municipalities;

    private static final Map<String, String> CITY_TO_MUNICIPALITY = Map.of(
            "Visby", "Gotland"
    );

    public MunicipalityService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://taxonomy.api.jobtechdev.se")
                .build();
    }

    public synchronized String getMunicipalityId(String location) {

        if (location == null || location.isBlank()) {
            return null;
        }

        if (municipalities == null) {
            loadMunicipalities();
        }

        String municipalityName = CITY_TO_MUNICIPALITY.getOrDefault(
                location.trim(),
                location.trim()
        );

        String municipalityId = municipalities.get(municipalityName);

        if (municipalityId == null) {
            throw new IllegalArgumentException(
                    "Unknown city or municipality: " + location
            );
        }

        return municipalityId;
    }

    public synchronized List<String> getMunicipalities() {

        if (municipalities == null) {
            loadMunicipalities();
        }

        return municipalities.keySet()
                .stream()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private void loadMunicipalities() {

        String query = """
                query MyQuery {
                  concepts(id: "i46j_HmG_v64") {
                    id
                    preferred_label
                    type
                    narrower {
                      id
                      preferred_label
                      type
                      narrower {
                        id
                        preferred_label
                        type
                      }
                    }
                  }
                }
                """;

        JsonNode response = restClient.get()
                .uri(builder -> builder
                        .path("/v1/taxonomy/graphql")
                        .queryParam("query", "{query}")
                        .build(query))
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Taxonomy API returned no response"
            );
        }

        if (!response.path("errors").isMissingNode()) {
            throw new IllegalStateException(
                    "Taxonomy GraphQL error: " + response.path("errors")
            );
        }

        Map<String, String> loaded =
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (JsonNode country : response.path("data").path("concepts")) {
            for (JsonNode region : country.path("narrower")) {
                for (JsonNode municipality : region.path("narrower")) {

                    if (!"municipality".equals(
                            municipality.path("type").asText()
                    )) {
                        continue;
                    }

                    String name = municipality
                            .path("preferred_label")
                            .asText();

                    String id = municipality
                            .path("id")
                            .asText();

                    if (!name.isBlank() && !id.isBlank()) {
                        loaded.put(name, id);
                    }
                }
            }
        }

        if (loaded.isEmpty()) {
            throw new IllegalStateException(
                    "Taxonomy returned no municipalities: " + response
            );
        }

        municipalities = Collections.unmodifiableMap(loaded);
    }
}

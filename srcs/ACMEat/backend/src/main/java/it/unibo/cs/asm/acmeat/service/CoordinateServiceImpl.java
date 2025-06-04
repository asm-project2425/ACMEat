package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.model.util.Coordinate;
import it.unibo.cs.asm.acmeat.service.repository.CoordinateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class CoordinateServiceImpl implements CoordinateService {
    private final RestClient restClient;
    private static final String DISTANCE_PATH = "/api/coordinates/distance";

    public CoordinateServiceImpl(RestClient.Builder restClientBuilder, @Value("${rest.gis.base-url}") String
            gisBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(gisBaseUrl).build();
    }

    public double distanceBetween(Coordinate a, Coordinate b) {
        return Optional.ofNullable(
                restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(DISTANCE_PATH)
                                .queryParam("lat1", a.latitude())
                                .queryParam("lon1", a.longitude())
                                .queryParam("lat2", b.latitude())
                                .queryParam("lon2", b.longitude())
                                .build())
                        .retrieve()
                        .body(Double.class)
        ).orElseThrow(() -> new IllegalStateException("Error calculating distance between coordinates: " + a +
                " and " + b));
    }
}

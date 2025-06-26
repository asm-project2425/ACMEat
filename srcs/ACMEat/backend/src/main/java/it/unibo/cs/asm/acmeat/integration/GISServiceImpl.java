package it.unibo.cs.asm.acmeat.integration;

import it.unibo.cs.asm.acmeat.model.Coordinate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class GISServiceImpl implements GISService {
    private final RestClient restClient;
    private static final String DISTANCE_PATH = "/api/v1/distance";
    private static final String LOCATE_PATH = "/api/v1/locate";

    public GISServiceImpl(RestClient.Builder restClientBuilder,
                          @Value("${rest.gis.base-url}") String gisBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(gisBaseUrl).build();
    }

    @Override
    public double calculateDistance(Coordinate a, Coordinate b) {
        return Optional.ofNullable(
                        restClient.get().uri(uriBuilder -> uriBuilder.path(DISTANCE_PATH)
                                        .queryParam("lat1", a.latitude())
                                        .queryParam("lon1", a.longitude())
                                        .queryParam("lat2", b.latitude())
                                        .queryParam("lon2", b.longitude())
                                        .build())
                                .retrieve()
                                .body(DistanceResponse.class)
                ).map(DistanceResponse::distance)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve distance between coordinates: " + a +
                        " and " + b));
    }

    private record DistanceResponse(double distance) {}

    @Override
    public Coordinate getCoordinates(String address, Double lat, Double lon) {
        return Optional.ofNullable(
                        restClient.get().uri(uriBuilder -> {
                                    var builder = uriBuilder.path(LOCATE_PATH)
                                            .queryParam("query", address);
                                    if (lat != null && lon != null) {
                                        builder.queryParam("lat", lat)
                                                .queryParam("lon", lon);
                                    }
                                    return builder.build();
                                })
                                .retrieve()
                                .body(GeocodeResponse.class)
                ).map(response -> new Coordinate(response.lat(), response.lon()))
                .orElseThrow(() -> new IllegalStateException("Failed to geocode address: " + address +
                        (lat != null && lon != null ? " near [" + lat + ", " + lon + "]" : "")));
    }

    private record GeocodeResponse(double lat, double lon) {}
}

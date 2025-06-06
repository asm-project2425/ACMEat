package it.unibo.cs.asm.acmeat.camunda.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.camunda.utility.ZeebeService;
import it.unibo.cs.asm.acmeat.service.abstractions.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static it.unibo.cs.asm.acmeat.camunda.utility.ProcessConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class RestaurantWorkers {
    private final ZeebeService zeebeService;
    private final RestClient.Builder restClientBuilder;

    private static final String AVAIBLE_PATH = "/avaible.php";

    @JobWorker(type = JOB_CHECK_RESTAURANT_AVAILABILITY)
    public void checkRestaurantAvailability(@Variable String correlationKey, @Variable String restaurantBaseUrl,
                                            @Variable int orderId, @Variable String deliveryTime) {
        RestClient restClient = restClientBuilder.baseUrl(restaurantBaseUrl).build();

        AvailabilityResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(AVAIBLE_PATH)
                        .queryParam("id_ordine", orderId)
                        .queryParam("orario", deliveryTime)
                        .build())
                .retrieve()
                .body(AvailabilityResponse.class);

        if (response == null) {
            log.warn("Null response received from {} for order {}", restaurantBaseUrl, orderId);
        }
        boolean isAvailable = response != null && response.avaible;

        zeebeService.sendMessage(MSG_RESTAURANT_AVAILABILITY, correlationKey, Map.of(VAR_RESTAURANT_AVAILABILITY,
                isAvailable));
    }

    private record AvailabilityResponse(boolean avaible) {}
}

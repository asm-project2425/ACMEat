package it.unibo.cs.asm.acmeat.process.orderManagement.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.process.common.ZeebeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static it.unibo.cs.asm.acmeat.process.common.ProcessConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class RestaurantWorkers {
    private final ZeebeService zeebeService;
    private final RestClient.Builder restClientBuilder;

    private static final String AVAIBLE_PATH = "/avaible.php";
    public static final String CANCEL_ORDER_PATH = "/cancel_order.php";

    @JobWorker(type = JOB_CHECK_RESTAURANT_AVAILABILITY)
    public void checkRestaurantAvailability(@Variable String restaurantBaseUrl, @Variable int orderId,
                                            @Variable String deliveryTime) {
        RestClient restClient = restClientBuilder.baseUrl(restaurantBaseUrl).build();

        boolean isAvailable = false;

        try {
            AvailabilityResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(AVAIBLE_PATH)
                            .queryParam("id_ordine", orderId)
                            .queryParam("orario", deliveryTime)
                            .build())
                    .retrieve()
                    .body(AvailabilityResponse.class);
            isAvailable = response != null && response.avaible();
        } catch (Exception e) {
            log.warn("Failed to check availability for order {} at restaurant {}: {}", orderId, restaurantBaseUrl, e.getMessage());
        }

        zeebeService.sendMessage(MSG_RESTAURANT_AVAILABILITY, String.valueOf(orderId), Map.of(
                VAR_RESTAURANT_AVAILABILITY, isAvailable));
    }

    private record AvailabilityResponse(boolean avaible) {}

    @JobWorker(type = JOB_CANCEL_RESTAURANT_ORDER)
    public void cancelRestaurantOrder(@Variable String restaurantBaseUrl, @Variable int orderId) {
        RestClient restClient = restClientBuilder.baseUrl(restaurantBaseUrl).build();

        try {
            restClient.get()
                    .uri(uriBuilder -> uriBuilder.path(CANCEL_ORDER_PATH)
                            .queryParam("id_ordine", orderId)
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to cancel order {} at restaurant {}: {}", orderId, restaurantBaseUrl, e.getMessage());
        }
    }
}
package it.unibo.cs.asm.acmeat.process.orderManagement.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.process.common.ZeebeService;
import it.unibo.cs.asm.acmeat.dto.entities.CoordinateDTO;
import it.unibo.cs.asm.acmeat.dto.entities.ShippingCompanyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static it.unibo.cs.asm.acmeat.process.common.ProcessConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class ShippingCompanyWorkers {
    private final RestClient.Builder restClientBuilder;

    private static final String AVAILABLE_PATH = "/api/v1/available";
    private static final String CANCELLATION_PATH = "/api/v1/cancel";
    private static final String CONFIRMATION_PATH = "/api/v1/confirm";

    @JobWorker(type = JOB_SHIPPING_SERVICE_AVAILABILITY_REQUEST)
    public Map<String, String> shippingCompanyAvailabilityRequest(@Variable String correlationKey,
                                                                  @Variable ShippingCompanyDTO shippingCompany,
                                                                  @Variable int orderId,
                                                                  @Variable String deliveryTime,
                                                                  @Variable CoordinateDTO restaurantPosition,
                                                                  @Variable String deliveryAddress) {
        String shippingCompanyCorrelationKey = correlationKey + "+" + shippingCompany.getId();
        RestClient restClient = restClientBuilder.baseUrl(shippingCompany.getBaseUrl()).build();

        ShippingAvailabilityRequest request = new ShippingAvailabilityRequest(shippingCompanyCorrelationKey, orderId,
                deliveryTime, restaurantPosition, deliveryAddress);

        try {
            restClient.post().uri(AVAILABLE_PATH).body(request).retrieve().toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to contact shipping service at {} for correlationKey {}: {}", shippingCompany.getBaseUrl(),
                    correlationKey, e.getMessage());
        }

        return Map.of(VAR_SHIPPING_COMPANY_CORRELATION_KEY, shippingCompanyCorrelationKey);
    }

    private record ShippingAvailabilityRequest(String correlationKey, int orderId, String deliveryTime,
                                               CoordinateDTO restaurantPosition, String deliveryAddress) {}

    @JobWorker(type = JOB_REQUEST_SHIPPING_CANCELLATION)
    public void requestShippingCancellation(@Variable String shippingCompanyBaseUrl, @Variable int orderId) {
        RestClient restClient = restClientBuilder.baseUrl(shippingCompanyBaseUrl).build();

        try {
            restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(CANCELLATION_PATH)
                            .queryParam("id_ordine", orderId)
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to request shipping cancellation for order {}: {}", orderId, e.getMessage());
        }
    }

    @JobWorker(type = JOB_CONFIRM_SHIPPING_COMPANY)
    public void confirmShippingCompany(@Variable String shippingCompanyBaseUrl, @Variable int orderId) {
        RestClient restClient = restClientBuilder.baseUrl(shippingCompanyBaseUrl).build();

        try {
            restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(CONFIRMATION_PATH)
                            .queryParam("id_ordine", orderId)
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to confirm shipping for order {}: {}", orderId, e.getMessage());
        }
    }
}

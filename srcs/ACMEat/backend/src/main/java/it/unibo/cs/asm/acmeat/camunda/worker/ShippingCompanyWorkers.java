package it.unibo.cs.asm.acmeat.camunda.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.camunda.utility.ZeebeService;
import it.unibo.cs.asm.acmeat.dto.entities.CoordinateDTO;
import it.unibo.cs.asm.acmeat.dto.entities.ShippingCompanyDTO;
import it.unibo.cs.asm.acmeat.model.util.Coordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static it.unibo.cs.asm.acmeat.camunda.utility.ProcessConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class ShippingCompanyWorkers {
    private final ZeebeService zeebeService;
    private final RestClient.Builder restClientBuilder;

    private static final String AVAILABLE_PATH = "/api/v1/available";

    @JobWorker(type = JOB_SHIPPING_SERVICE_AVAILABILITY_REQUEST)
    public Map<String, String> shippingCompanyAvailabilityRequest(@Variable ShippingCompanyDTO shippingCompany,
                                                                  @Variable String deliveryTime,
                                                                  @Variable CoordinateDTO restaurantPosition,
                                                                  @Variable String deliveryAddress) {
        String correlationKey = String.valueOf(shippingCompany.getId()); // Assuming the shipping company ID is used as the correlation key
//        RestClient restClient = restClientBuilder.baseUrl(shippingCompany.getBaseUrl()).build();
//
//        ShippingAvailabilityRequest request = new ShippingAvailabilityRequest(correlationKey, deliveryTime,
//                restaurantPosition, deliveryAddress);
//
//        try {
//            restClient.post().uri(AVAILABLE_PATH).body(request).retrieve().toBodilessEntity();
//        } catch (Exception e) {
//            log.warn("Failed to contact shipping service at {} for correlationKey {}: {}", shippingCompany.getBaseUrl(),
//                    correlationKey, e.getMessage());
//        }

        return Map.of(VAR_SHIPPING_COMPANY_CORRELATION_KEY, correlationKey);
    }

    private record ShippingAvailabilityRequest(String correlationKey, String deliveryTime,
                                               CoordinateDTO restaurantPosition, String deliveryAddress) {}

    @JobWorker(type = JOB_REQUEST_SHIPPING_CANCELLATION)
    public void requestShippingCancellation(@Variable int orderId, @Variable String shippingCompanyId) {
        // TODO: chiamare l'api per richiedere la cancellazione della spedizione
    }
}

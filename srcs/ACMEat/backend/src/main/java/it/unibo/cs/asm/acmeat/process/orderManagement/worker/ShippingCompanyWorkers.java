package it.unibo.cs.asm.acmeat.process.orderManagement.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.dto.entities.ShippingCompanyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.*;
import java.time.format.DateTimeFormatter;
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
                                                                  @Variable String restaurantAddress,
                                                                  @Variable String deliveryAddress) {
        String shippingCompanyCorrelationKey = correlationKey + "+" + shippingCompany.getId();
        RestClient restClient = restClientBuilder.baseUrl(shippingCompany.getBaseUrl()).build();

        ShippingAvailabilityRequest request = new ShippingAvailabilityRequest(shippingCompanyCorrelationKey, orderId,
                convertToUtc(deliveryTime), restaurantAddress, deliveryAddress);

        try {
            restClient.post().uri(AVAILABLE_PATH).body(request).retrieve().toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to contact shipping service at {} for correlationKey {}: {}", shippingCompany.getBaseUrl(),
                    correlationKey, e.getMessage());
        }

        return Map.of(VAR_SHIPPING_COMPANY_CORRELATION_KEY, shippingCompanyCorrelationKey);
    }

    private String convertToUtc(String deliveryTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.parse(deliveryTime, timeFormatter);
        LocalDate today = LocalDate.now();
        ZoneId zoneRome = ZoneId.of("Europe/Rome");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(today, localTime, zoneRome);
        ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);

        return utcDateTime.format(timeFormatter);
    }

    private record ShippingAvailabilityRequest(String correlationKey, int orderId, String deliveryTime,
                                               String restaurantAddress, String deliveryAddress) {}

    @JobWorker(type = JOB_CONFIRM_SHIPPING_COMPANY)
    public void confirmShippingCompany(@Variable String shippingCompanyBaseUrl, @Variable int deliveryId) {
        RestClient restClient = restClientBuilder.baseUrl(shippingCompanyBaseUrl).build();
        ShippingConfirmationRequest confirmationRequest = new ShippingConfirmationRequest(deliveryId);

        try {
            restClient.post()
                    .uri(CONFIRMATION_PATH).body(confirmationRequest).retrieve().toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to confirm shipping for deliveryId {}: {}", deliveryId, e.getMessage());
        }
    }

    private record ShippingConfirmationRequest(int deliveryId) {}

    @JobWorker(type = JOB_REQUEST_SHIPPING_CANCELLATION)
    public void requestShippingCancellation(@Variable String shippingCompanyBaseUrl, @Variable int deliveryId) {
        RestClient restClient = restClientBuilder.baseUrl(shippingCompanyBaseUrl).build();
        ShippingCancellationRequest cancellationRequest = new ShippingCancellationRequest(deliveryId);

        try {
            restClient.post().uri(CANCELLATION_PATH).body(cancellationRequest).retrieve().toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to request shipping cancellation for deliveryId {}: {}", deliveryId, e.getMessage());
        }
    }

    private record ShippingCancellationRequest(int deliveryId) {}
}

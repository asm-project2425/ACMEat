package it.unibo.cs.asm.acmeat.process.restaurantManagement.worker;

import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;
import it.unibo.cs.asm.acmeat.dto.response.RequestRestaurantInformationResponse;
import it.unibo.cs.asm.acmeat.process.common.ZeebeService;
import it.unibo.cs.asm.acmeat.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.unibo.cs.asm.acmeat.process.common.ProcessConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class AcmeRMWorkers {
    private final ZeebeService zeebeService;
    private final RestaurantService restaurantService;

    public RequestRestaurantInformationResponse retrieveRestaurantInformationManually(int restaurantId) {
        String correlationKey = UUID.randomUUID().toString();
        LocalTime currentTime = LocalTime.now(ZoneId.of("Europe/Rome"));
        boolean isBetween22And10 = currentTime.isAfter(LocalTime.of(21, 0))
                || currentTime.isBefore(LocalTime.of(10, 0));

        zeebeService.sendMessage(MSG_REQUEST_RESTAURANT_INFORMATION, correlationKey, Map.of(
                VAR_CORRELATION_KEY, correlationKey, VAR_IS_BETWEEN_22_AND_10, isBetween22And10));

        if (!isBetween22And10) {
            throw new IllegalStateException(("The restaurant information cannot be edited at this time. " +
                    "Time available: 22:00 - 10:00 (current time: " + currentTime + ")"));
        }

        zeebeService.completeJob(JOB_RETRIEVE_RESTAURANT_INFORMATION, VAR_CORRELATION_KEY, correlationKey, Map.of());

        List<MenuDTO> menu = restaurantService.getMenuByRestaurantId(restaurantId);
        List<TimeSlotDTO> timeSlots = restaurantService.getTimeSlotsByRestaurantId(restaurantId);

        return new RequestRestaurantInformationResponse(correlationKey, menu, timeSlots);
    }

    public void confirmRestaurantInformationUpdateManually(String correlationKey) {
        zeebeService.sendMessage(MSG_RESTAURANT_INFORMATION_UPDATED, correlationKey, Map.of());
        zeebeService.completeJob(JOB_UPDATE_RESTAURANT_INFORMATION, VAR_CORRELATION_KEY, correlationKey, Map.of());
    }
}

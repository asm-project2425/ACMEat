package it.unibo.cs.asm.acmeat.camunda.api;

import it.unibo.cs.asm.acmeat.camunda.utility.ZeebeService;
import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.RestaurantDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;
import it.unibo.cs.asm.acmeat.dto.request.UpdateRestaurantInformationRequest;
import it.unibo.cs.asm.acmeat.dto.response.RequestRestaurantInformationResponse;
import it.unibo.cs.asm.acmeat.dto.response.UpdateRestaurantInformationResponse;
import it.unibo.cs.asm.acmeat.service.abstractions.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.unibo.cs.asm.acmeat.camunda.utility.ProcessConstants.*;

@RequiredArgsConstructor
@RequestMapping("api/v1/restaurants/{restaurantId}/information")
@RestController
public class RestaurantManagementController {
    private final ZeebeService zeebeService;
    private final RestaurantService restaurantService;

    private String correlationKey;

    @GetMapping()
    public ResponseEntity<RequestRestaurantInformationResponse> retrieveRestaurantInformation(
            @PathVariable int restaurantId) {
        correlationKey = UUID.randomUUID().toString();
        zeebeService.sendMessage(MSG_REQUEST_RESTAURANT_INFORMATION, correlationKey, Map.of(CORRELATION_KEY,
                correlationKey));
        if (zeebeService.completeJob(JOB_RETRIEVE_RESTAURANT_INFORMATION, correlationKey, Map.of())) {
            RestaurantDTO restaurant = restaurantService.getRestaurantDTOById(restaurantId);
            List<MenuDTO> menu = restaurantService.getMenuByRestaurantId(restaurantId);
            List<TimeSlotDTO> timeSlots = restaurantService.getTimeSlotsByRestaurantId(restaurantId);

            return ResponseEntity.ok(new RequestRestaurantInformationResponse(restaurant, menu, timeSlots));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping()
    public ResponseEntity<UpdateRestaurantInformationResponse> updateRestaurantInformation(
            @PathVariable int restaurantId,
            @RequestBody UpdateRestaurantInformationRequest request) {
        zeebeService.sendMessage(MSG_RESTAURANT_INFORMATION_UPDATED, correlationKey, Map.of());
        if (zeebeService.completeJob(JOB_UPDATE_RESTAURANT_INFORMATION, correlationKey, Map.of())) {
            boolean updatedMenu = restaurantService.setMenuByRestaurantId(restaurantId, request.menus());
            boolean updatedTimeSlots = restaurantService.setTimeSlotsByRestaurantId(restaurantId,
                    request.timeSlots());

            return ResponseEntity.ok(new UpdateRestaurantInformationResponse(updatedMenu && updatedTimeSlots));
        }
        return ResponseEntity.notFound().build();
    }
}

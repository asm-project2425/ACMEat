package it.unibo.cs.asm.acmeat.camunda.api;

import it.unibo.cs.asm.acmeat.camunda.utility.ZeebeService;
import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;
import it.unibo.cs.asm.acmeat.dto.request.CreateMenuRequest;
import it.unibo.cs.asm.acmeat.dto.response.RequestRestaurantInformationResponse;
import it.unibo.cs.asm.acmeat.service.abstractions.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.unibo.cs.asm.acmeat.camunda.utility.ProcessConstants.*;

@RequiredArgsConstructor
@RequestMapping("api/v1/restaurants")
@RestController
public class RestaurantManagementController {
    private final ZeebeService zeebeService;
    private final RestaurantService restaurantService;

    @GetMapping("/{restaurantId}/information")
    public ResponseEntity<RequestRestaurantInformationResponse> retrieveRestaurantInformation(
            @PathVariable int restaurantId) {
        String correlationKey = UUID.randomUUID().toString();
        zeebeService.sendMessage(MSG_REQUEST_RESTAURANT_INFORMATION, correlationKey, Map.of(CORRELATION_KEY,
                correlationKey));
        if (!zeebeService.completeJob(JOB_RETRIEVE_RESTAURANT_INFORMATION, correlationKey, Map.of())) {
            throw new IllegalStateException("Restaurant information can only be updated between 22:00 and 10:00.");
        }
        List<MenuDTO> menu = restaurantService.getMenuByRestaurantId(restaurantId);
        List<TimeSlotDTO> timeSlots = restaurantService.getTimeSlotsByRestaurantId(restaurantId);

        return ResponseEntity.ok(new RequestRestaurantInformationResponse(correlationKey, menu, timeSlots));
    }

    @PostMapping("/{restaurantId}/menus")
    public ResponseEntity<MenuDTO> addMenu(@PathVariable int restaurantId, @RequestBody CreateMenuRequest request) {
        MenuDTO menu = restaurantService.addMenuToRestaurant(restaurantId, request.name(), request.price());
        return ResponseEntity.ok(menu);
    }

    @PutMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<MenuDTO> updateMenu(@PathVariable int restaurantId, @PathVariable int menuId,
                                              @RequestBody CreateMenuRequest request) {
        MenuDTO updated = restaurantService.updateMenu(restaurantId, menuId, request.name(), request.price());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable int restaurantId, @PathVariable int menuId) {
        restaurantService.deleteMenu(restaurantId, menuId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{restaurantId}/timeslots/{timeSlotId}")
    public ResponseEntity<TimeSlotDTO> updateTimeSlot(@PathVariable int restaurantId, @PathVariable int timeSlotId,
                                                      @RequestParam boolean active) {
        TimeSlotDTO updated = restaurantService.updateTimeSlot(restaurantId, timeSlotId, active);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Boolean> confirmRestaurantInformationUpdate(
            @RequestParam String correlationKey) {
        zeebeService.sendMessage(MSG_RESTAURANT_INFORMATION_UPDATED, correlationKey, Map.of());
        boolean success = zeebeService.completeJob(JOB_UPDATE_RESTAURANT_INFORMATION, correlationKey, Map.of());

        return ResponseEntity.ok(success);
    }
}

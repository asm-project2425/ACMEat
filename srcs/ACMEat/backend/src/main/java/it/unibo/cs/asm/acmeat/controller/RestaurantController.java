package it.unibo.cs.asm.acmeat.controller;

import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;
import it.unibo.cs.asm.acmeat.dto.request.CreateMenuRequest;
import it.unibo.cs.asm.acmeat.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("api/v1/restaurants")
@RestController
public class RestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping("/{restaurantId}/menus")
    public ResponseEntity<MenuDTO> addMenu(@PathVariable int restaurantId,
                                           @RequestBody @Valid CreateMenuRequest request) {
        MenuDTO menu = restaurantService.addMenuToRestaurant(restaurantId, request.name(), request.price());
        return ResponseEntity.ok(menu);
    }

    @PutMapping("/{restaurantId}/menus/{menuId}")
    public ResponseEntity<MenuDTO> updateMenu(@PathVariable int restaurantId, @PathVariable int menuId,
                                              @RequestBody @Valid CreateMenuRequest request) {
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
}

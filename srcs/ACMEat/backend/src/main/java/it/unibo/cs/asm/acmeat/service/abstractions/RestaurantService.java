package it.unibo.cs.asm.acmeat.service.abstractions;

import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.RestaurantDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;
import it.unibo.cs.asm.acmeat.model.Restaurant;

import java.util.List;

public interface RestaurantService {

    Restaurant getRestaurantById(int id);

    List<RestaurantDTO> getRestaurantsByCityId(int cityId);

    List<MenuDTO> getMenuByRestaurantId(int restaurantId);

    List<TimeSlotDTO> getActiveTimeSlotsByRestaurantId(int restaurantId);

    List<TimeSlotDTO> getTimeSlotsByRestaurantId(int restaurantId);

    MenuDTO addMenuToRestaurant(int restaurantId, String name, double price);

    MenuDTO updateMenu(int restaurantId, int menuId, String name, double price);

    void deleteMenu(int restaurantId, int menuId);

    TimeSlotDTO updateTimeSlot(int restaurantId, int timeSlotId, boolean active);
}

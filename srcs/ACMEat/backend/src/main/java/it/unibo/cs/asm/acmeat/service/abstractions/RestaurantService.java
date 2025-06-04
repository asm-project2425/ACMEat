package it.unibo.cs.asm.acmeat.service.abstractions;

import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.RestaurantDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;

import java.util.List;

public interface RestaurantService {

    RestaurantDTO getRestaurantDTOById(int id);

    List<RestaurantDTO> getRestaurantsByCityId(int cityId);

    List<MenuDTO> getMenuByRestaurantId(int restaurantId);

    List<TimeSlotDTO> getTimeSlotsByRestaurantId(int restaurantId);

    boolean setMenuByRestaurantId(int restaurantId, List<MenuDTO> menus);

    boolean setTimeSlotsByRestaurantId(int restaurantId, List<TimeSlotDTO> timeSlots);

}

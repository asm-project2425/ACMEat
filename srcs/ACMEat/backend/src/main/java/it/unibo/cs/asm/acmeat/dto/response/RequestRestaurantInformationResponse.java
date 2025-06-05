package it.unibo.cs.asm.acmeat.dto.response;

import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.RestaurantDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;

import java.util.List;

public record RequestRestaurantInformationResponse(String correlationKey, List<MenuDTO> menus,
                                                   List<TimeSlotDTO> timeSlots) {
}

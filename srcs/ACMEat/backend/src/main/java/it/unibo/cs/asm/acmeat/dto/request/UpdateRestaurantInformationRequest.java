package it.unibo.cs.asm.acmeat.dto.request;

import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;

import java.util.List;

public record UpdateRestaurantInformationRequest(String correlationKey, List<MenuDTO> menus,
                                                 List<TimeSlotDTO> timeSlots) {
}

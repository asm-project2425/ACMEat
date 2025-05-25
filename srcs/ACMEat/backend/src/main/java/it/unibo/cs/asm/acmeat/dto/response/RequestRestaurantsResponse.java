package it.unibo.cs.asm.acmeat.dto.response;

import it.unibo.cs.asm.acmeat.dto.entities.RestaurantDTO;

import java.util.List;

public record RequestRestaurantsResponse(List<RestaurantDTO> restaurants) {
}

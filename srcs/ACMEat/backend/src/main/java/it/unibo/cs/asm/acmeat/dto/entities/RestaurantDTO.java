package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantDTO {
    private final int id;
    private final String name;

    public RestaurantDTO(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
    }
}

package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.City;
import lombok.Getter;

@Getter
public class CityDTO {
    private final int id;
    private final String name;

    public CityDTO(City city) {
        this.id = city.getId();
        this.name = city.getName();
    }
}

package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.util.Coordinate;
import lombok.Getter;

@Getter
public class CoordinateDTO {
    private final double latitude;
    private final double longitude;

    public CoordinateDTO(Coordinate coordinate) {
        this.latitude = coordinate.latitude();
        this.longitude = coordinate.longitude();
    }
}

package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.util.Coordinate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CoordinateDTO {
    private double latitude;
    private double longitude;

    public CoordinateDTO(Coordinate coordinate) {
        this.latitude = coordinate.latitude();
        this.longitude = coordinate.longitude();
    }
}

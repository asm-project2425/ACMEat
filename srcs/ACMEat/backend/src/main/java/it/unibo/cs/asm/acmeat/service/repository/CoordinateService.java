package it.unibo.cs.asm.acmeat.service.repository;

import it.unibo.cs.asm.acmeat.model.util.Coordinate;

public interface CoordinateService {

    double distanceBetween(Coordinate a, Coordinate b);
}

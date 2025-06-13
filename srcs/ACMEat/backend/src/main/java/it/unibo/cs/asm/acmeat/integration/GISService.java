package it.unibo.cs.asm.acmeat.integration;

import it.unibo.cs.asm.acmeat.model.Coordinate;

public interface GISService {
    /**
     * Calculates the distance between two coordinates
     *
     * @param a the first coordinate
     * @param b the second coordinate
     * @return the distance in meters
     */
    double distanceBetween(Coordinate a, Coordinate b);
}

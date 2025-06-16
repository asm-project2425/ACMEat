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
    double calculateDistance(Coordinate a, Coordinate b);

    /**
     * Geocodes an address to its coordinates
     *
     * @param address the address to geocode
     * @return the coordinates of the address
     */
    Coordinate getCoordinates(String address);
}

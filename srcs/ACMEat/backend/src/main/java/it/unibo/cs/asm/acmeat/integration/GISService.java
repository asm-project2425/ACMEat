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
     * Geocodes an address to its coordinates. If latitude and longitude are provided,
     * they are used to prioritize the closest match to the specified location.
     *
     * @param address the address to geocode
     * @param lat optional latitude used to disambiguate results (can be null)
     * @param lon optional longitude used to disambiguate results (can be null)
     * @return the coordinates of the address, possibly the closest match to the given location
     * @throws IllegalStateException if the address cannot be geocoded
     */
    Coordinate getCoordinates(String address, Double lat, Double lon);

}

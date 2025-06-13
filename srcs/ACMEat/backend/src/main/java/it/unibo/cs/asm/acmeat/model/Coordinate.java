package it.unibo.cs.asm.acmeat.model;

public record Coordinate(double latitude, double longitude) {

    public Coordinate {
        if (latitude < -90 || latitude > 90)
            throw new IllegalArgumentException("Invalid latitude value: " + latitude);
        if (longitude < -180 || longitude > 180)
            throw new IllegalArgumentException("Invalid longitude value: " + longitude);
    }
}

package it.unibo.cs.asm.acmeat.exception;

public class RestaurantUpdateNotAllowedException extends RuntimeException {
    public RestaurantUpdateNotAllowedException() {
        super("Restaurant information can only be updated between 22:00 and 10:00.");
    }
}

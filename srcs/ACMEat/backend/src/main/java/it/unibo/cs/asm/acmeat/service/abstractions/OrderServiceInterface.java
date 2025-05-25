package it.unibo.cs.asm.acmeat.service.abstractions;

import it.unibo.cs.asm.acmeat.dto.entities.OrderDTO;

public interface OrderServiceInterface {
    OrderDTO createOrder(int restaurantId, int menuId, int timeSlotId, String address);
}

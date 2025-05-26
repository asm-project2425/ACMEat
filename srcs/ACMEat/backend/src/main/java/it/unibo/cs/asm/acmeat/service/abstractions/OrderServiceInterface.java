package it.unibo.cs.asm.acmeat.service.abstractions;

import it.unibo.cs.asm.acmeat.dto.entities.OrderDTO;
import it.unibo.cs.asm.acmeat.dto.request.OrderedItemRequest;

import java.util.List;

public interface OrderServiceInterface {
    OrderDTO createOrder(int restaurantId, List<OrderedItemRequest> items, int timeSlotId, String address);
}

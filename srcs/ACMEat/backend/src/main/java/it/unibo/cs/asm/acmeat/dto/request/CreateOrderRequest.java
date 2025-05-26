package it.unibo.cs.asm.acmeat.dto.request;

import java.util.List;

public record CreateOrderRequest(int restaurantId, List<OrderedItemRequest> items, int timeSlotId,
                                 String deliveryAddress) {
}

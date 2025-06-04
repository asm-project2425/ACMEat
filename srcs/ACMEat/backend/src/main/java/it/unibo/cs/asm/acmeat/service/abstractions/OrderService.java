package it.unibo.cs.asm.acmeat.service.abstractions;

import it.unibo.cs.asm.acmeat.dto.entities.OrderDTO;
import it.unibo.cs.asm.acmeat.dto.request.OrderedItemRequest;
import it.unibo.cs.asm.acmeat.model.ShippingCompany;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(int restaurantId, List<OrderedItemRequest> items, int timeSlotId, String address);

    void setShippingCompany(int orderId, ShippingCompany shippingCompany);

    void cancelOrder(int orderId);

    void orderActivated(int orderId);

    void orderDelivered(int orderId);
}

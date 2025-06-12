package it.unibo.cs.asm.acmeat.service.abstractions;

import it.unibo.cs.asm.acmeat.dto.entities.OrderDTO;
import it.unibo.cs.asm.acmeat.dto.request.OrderedItemRequest;
import it.unibo.cs.asm.acmeat.model.Order;
import it.unibo.cs.asm.acmeat.model.OrderStatus;
import it.unibo.cs.asm.acmeat.model.ShippingCompany;

import java.util.List;

public interface OrderService {
    Order getOrderById(int orderId);

    OrderDTO createOrder(int restaurantId, List<OrderedItemRequest> items, int timeSlotId, String address);

    void setShippingCompany(int orderId, ShippingCompany shippingCompany);

    void updateOrderStatus(int orderId, OrderStatus status);
}

package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.OrderDTO;
import it.unibo.cs.asm.acmeat.dto.request.OrderedItemRequest;
import it.unibo.cs.asm.acmeat.model.Order;
import it.unibo.cs.asm.acmeat.model.OrderStatus;
import it.unibo.cs.asm.acmeat.model.ShippingCompany;

import java.util.List;

public interface OrderService {
    /**
     * Retrive the order by its ID.
     *
     * @param orderId the ID of the order
     * @return the Order object if found, otherwise throws an exception
     */
    Order getOrderById(int orderId);

    /**
     * Creates a new order with the specified restaurant, items, time slot, and address.
     *
     * @param restaurantId the ID of the restaurant
     * @param items        the list of ordered items
     * @param timeSlotId   the ID of the time slot
     * @param address      the delivery address
     * @return the created OrderDTO
     */
    OrderDTO createOrder(int restaurantId, List<OrderedItemRequest> items, int timeSlotId, String address);

    /**
     * Sets the shipping company for the specified order.
     *
     * @param orderId         the ID of the order
     * @param shippingCompany the shipping company to set
     */
    void setShippingCompany(int orderId, ShippingCompany shippingCompany);

    /**
     * Updates the status of the specified order.
     *
     * @param orderId the ID of the order
     * @param status  the new status to set
     */
    void updateOrderStatus(int orderId, OrderStatus status);

    /**
     * Saves the payment ID associated with the specified order.
     *
     * @param orderId   the ID of the order
     * @param paymentId the payment ID to save
     */
    void savePaymentId(int orderId, int paymentId);
}

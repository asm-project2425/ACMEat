package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.Order;
import lombok.Getter;

@Getter
public class OrderDTO {
    private final int id;
    private final RestaurantDTO restaurant;
    private final MenuDTO menu;
    private final TimeSlotDTO timeSlot;
    private final String deliveryAddress;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.restaurant = new RestaurantDTO(order.getRestaurant());
        this.menu = new MenuDTO(order.getMenu());
        this.timeSlot = new TimeSlotDTO(order.getTimeSlot());
        this.deliveryAddress = order.getDeliveryAddress();
    }
}

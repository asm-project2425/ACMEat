package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.Order;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class OrderDTO {
    private final int id;
    private final String restaurantName;
    private final List<OrderedItemDTO> items;
    private final String price;
    private final String shippingPrice;
    private final String deliveryAddress;
    private final String deliveryTime;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.restaurantName = order.getRestaurant().getName();
        this.items = order.getItems().stream().map(OrderedItemDTO::new).toList();
        this.price = order.getPrice().toString();
        this.shippingPrice = order.getShippingPrice() != null ? order.getShippingPrice().toString() : "";
        this.deliveryAddress = order.getDeliveryAddress();
        this.deliveryTime = order.getDeliveryDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}

package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.Order;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class OrderDTO {
    private final int id;
    private final String restaurantName;
    private final List<OrderedItemDTO> items;
    private final String price;
    private final String deliveryAddress;
    private final String deliveryDateTime;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.restaurantName = order.getRestaurant().getName();
        this.items = order.getItems().stream().map(OrderedItemDTO::new).toList();
        this.price = order.getPrice().toString();
        this.deliveryAddress = order.getDeliveryAddress();
        this.deliveryDateTime = order.getDeliveryDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss O"));
    }
}

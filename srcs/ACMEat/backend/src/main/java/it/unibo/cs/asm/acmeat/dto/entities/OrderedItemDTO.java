package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.OrderedItem;
import lombok.Getter;

@Getter
public class OrderedItemDTO {
    private final String menuName;
    private final int quantity;

    public OrderedItemDTO(OrderedItem orderedItem) {
        this.menuName = orderedItem.getMenu().getName();
        this.quantity = orderedItem.getQuantity();
    }
}

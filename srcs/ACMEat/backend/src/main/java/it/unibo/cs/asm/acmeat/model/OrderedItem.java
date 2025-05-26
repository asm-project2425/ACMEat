package it.unibo.cs.asm.acmeat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ordered_items")
public class OrderedItem {
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    private Menu menu;
    private int quantity;
    @Setter
    @ManyToOne
    private Order order;

    public OrderedItem(Menu menu, int quantity) {
        this.menu = menu;
        this.quantity = quantity;
    }
}

package it.unibo.cs.asm.acmeat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    private ShippingCompany shippingCompany;
    @ManyToOne
    private Restaurant restaurant;
    @ManyToOne
    private Menu menu;
    @ManyToOne
    private TimeSlot timeSlot;
    private OffsetDateTime creationDateTime;
    private String deliveryAddress;
    @Column(precision = 12, scale = 2)
    private BigDecimal price;
    private OrderStatus status;

    public Order(ShippingCompany shippingCompany, Restaurant restaurant, Menu menu, TimeSlot timeSlot,
                 String deliveryAddress, BigDecimal price, OrderStatus status) {
        this.shippingCompany = shippingCompany;
        this.restaurant = restaurant;
        this.menu = menu;
        this.timeSlot = timeSlot;
        this.creationDateTime = OffsetDateTime.now();
        this.deliveryAddress = deliveryAddress;
        this.price = price;
        this.status = status;
    }
}

package it.unibo.cs.asm.acmeat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    private Restaurant restaurant;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderedItem> items;
    @ManyToOne
    private TimeSlot timeSlot;
    private OffsetDateTime creationDateTime;
    private OffsetDateTime deliveryDateTime;
    private String deliveryAddress;
    @Column(precision = 12, scale = 2)
    private BigDecimal price;
    @Setter
    @Column(precision = 12, scale = 2)
    private BigDecimal shippingPrice;
    @Setter
    private OrderStatus status;
    @Setter
    @ManyToOne
    private ShippingCompany shippingCompany;
    @Setter
    private int paymentId;

    public Order(Restaurant restaurant, List<OrderedItem> orderedItems, TimeSlot timeSlot, String deliveryAddress) {
        this.restaurant = restaurant;
        this.items = prepareItems(orderedItems);
        this.timeSlot = timeSlot;
        this.creationDateTime = OffsetDateTime.now();
        this.deliveryDateTime = calculateDeliveryDateTime(timeSlot);
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.CREATED;
    }

    // Calculate the delivery date and time based on the time slot's end time
    private OffsetDateTime calculateDeliveryDateTime(TimeSlot timeSlot) {
        return OffsetDateTime.now()
                .withHour(timeSlot.getEndTime().getHour())
                .withMinute(timeSlot.getEndTime().getMinute())
                .withSecond(0)
                .withNano(0)
                .withOffsetSameInstant(OffsetDateTime.now().getOffset());
    }

    // Prepare items for the order, setting the order reference and calculating the total price
    private List<OrderedItem> prepareItems(List<OrderedItem> orderedItems) {
        orderedItems.forEach(item -> item.setOrder(this));
        this.price = orderedItems.stream()
                .map(oi -> oi.getMenu().getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return orderedItems;
    }
}

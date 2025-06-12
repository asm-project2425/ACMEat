package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.OrderDTO;
import it.unibo.cs.asm.acmeat.dto.request.OrderedItemRequest;
import it.unibo.cs.asm.acmeat.model.*;
import it.unibo.cs.asm.acmeat.service.abstractions.OrderService;
import it.unibo.cs.asm.acmeat.service.abstractions.RestaurantService;
import it.unibo.cs.asm.acmeat.service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantService restaurantService;

    @Override
    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
    }

    @Override
    public OrderDTO createOrder(int restaurantId, List<OrderedItemRequest> items, int timeSlotId, String address) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        TimeSlot timeSlot = getTimeSlotById(restaurant, timeSlotId);
        List<OrderedItem> orderedItems = mapToOrderedItems(restaurant, items);

        Order order = new Order(restaurant, orderedItems, timeSlot, address);
        orderRepository.save(order);
        return new OrderDTO(order);
    }

    private TimeSlot getTimeSlotById(Restaurant restaurant, int timeSlotId) {
        return restaurant.getTimeSlots().stream()
                .filter(ts -> ts.getId() == timeSlotId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found with id: " + timeSlotId));
    }

    private Menu getMenuById(Restaurant restaurant, int menuId) {
        return restaurant.getMenus().stream()
                .filter(m -> m.getId() == menuId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + menuId));
    }

    private List<OrderedItem> mapToOrderedItems(Restaurant restaurant, List<OrderedItemRequest> items) {
        return items.stream()
                .map(item -> {
                    Menu menu = getMenuById(restaurant, item.menuId());
                    return new OrderedItem(menu, item.quantity());
                })
                .toList();
    }

    @Override
    public void setShippingCompany(int orderId, ShippingCompany shippingCompany) {
        Order order = getOrderById(orderId);
        order.setShippingCompany(shippingCompany);
        orderRepository.save(order);
    }

    @Override
    public void updateOrderStatus(int orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }
}

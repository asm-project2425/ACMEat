package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.OrderDTO;
import it.unibo.cs.asm.acmeat.model.*;
import it.unibo.cs.asm.acmeat.service.abstractions.OrderServiceInterface;
import it.unibo.cs.asm.acmeat.service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService implements OrderServiceInterface {
    private final OrderRepository orderRepository;
    private final RestaurantService restaurantService;

    @Override
    public OrderDTO createOrder(int restaurantId, int menuId, int timeSlotId, String address) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        Menu menu = restaurant.getMenus().stream()
                .filter(m -> m.getId() == menuId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + menuId));
        TimeSlot timeSlot = restaurant.getTimeSlots().stream()
                .filter(ts -> ts.getId() == timeSlotId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found with id: " + timeSlotId));
        Order order = new Order(null, restaurant, menu, timeSlot, address, menu.getPrice(),
                OrderStatus.CREATION);

        orderRepository.save(order);
        return new OrderDTO(order);
    }
}

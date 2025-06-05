package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.RestaurantDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;
import it.unibo.cs.asm.acmeat.model.Menu;
import it.unibo.cs.asm.acmeat.model.Restaurant;
import it.unibo.cs.asm.acmeat.model.TimeSlot;
import it.unibo.cs.asm.acmeat.service.abstractions.RestaurantService;
import it.unibo.cs.asm.acmeat.service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepository;

    @Override
    public Restaurant getRestaurantById(int id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with id: " + id));
    }

    @Override
    public List<RestaurantDTO> getRestaurantsByCityId(int cityId) {
        return restaurantRepository.findByCityId(cityId).stream().map(RestaurantDTO::new).toList();
    }

    @Override
    public List<MenuDTO> getMenuByRestaurantId(int restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(restaurant -> restaurant.getMenus().stream().map(MenuDTO::new).toList())
                .orElseGet(ArrayList::new);
    }

    @Override
    public List<TimeSlotDTO> getActiveTimeSlotsByRestaurantId(int restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(restaurant -> restaurant.getTimeSlots().stream()
                        .filter(TimeSlot::isActive)
                        .sorted(Comparator.comparing(TimeSlot::getStartTime))
                        .map(TimeSlotDTO::new).toList())
                .orElseGet(ArrayList::new);
    }

    @Override
    public List<TimeSlotDTO> getTimeSlotsByRestaurantId(int restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(restaurant -> restaurant.getTimeSlots().stream()
                        .sorted(Comparator.comparing(TimeSlot::getStartTime)).map(TimeSlotDTO::new).toList())
                .orElseGet(ArrayList::new);
    }

    @Override
    public MenuDTO addMenuToRestaurant(int restaurantId, String name, double price) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        Menu menu  = new Menu(name, BigDecimal.valueOf(price));
        if (!restaurant.addMenu(menu)) {
            throw new IllegalArgumentException("Menu with the same name already exists in this restaurant.");
        }
        restaurantRepository.save(restaurant);
        return restaurant.getMenus().stream()
                .filter(m -> m.getName().equals(name) && m.getPrice().compareTo(BigDecimal.valueOf(price)) == 0)
                .findFirst()
                .map(MenuDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("Failed to add menu: " + name));
    }

    @Override
    public MenuDTO updateMenu(int restaurantId, int menuId, String name, double price) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        if (!restaurant.updateMenu(menuId, name, price)) {
            throw new IllegalArgumentException("Menu with id " + menuId + " not found in restaurant with id " +
                    restaurantId);
        }
        restaurantRepository.save(restaurant);
        return restaurant.getMenus().stream()
                .filter(menu -> menu.getId() == menuId)
                .findFirst()
                .map(MenuDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("Menu with id " + menuId + " not found"));
    }

    @Override
    public void deleteMenu(int restaurantId, int menuId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        if (!restaurant.removeMenu(menuId)) {
            throw new IllegalArgumentException("Menu with id " + menuId + " not found in restaurant with id " +
                    restaurantId);
        }
        restaurantRepository.save(restaurant);
    }

    @Override
    public TimeSlotDTO updateTimeSlot(int restaurantId, int timeSlotId, boolean active) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        if (!restaurant.updateTimeSlot(timeSlotId, active)) {
            throw new IllegalArgumentException("Time slot with id " + timeSlotId + " not found in restaurant with id " +
                    restaurantId);
        }
        restaurantRepository.save(restaurant);
        return restaurant.getTimeSlots().stream()
                .filter(ts -> ts.getId() == timeSlotId)
                .findFirst()
                .map(TimeSlotDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("Time slot with id " + timeSlotId + " not found"));
    }
}

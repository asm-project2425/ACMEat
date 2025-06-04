package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.RestaurantDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;
import it.unibo.cs.asm.acmeat.model.Restaurant;
import it.unibo.cs.asm.acmeat.model.TimeSlot;
import it.unibo.cs.asm.acmeat.service.abstractions.RestaurantService;
import it.unibo.cs.asm.acmeat.service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public Restaurant getRestaurantById(int id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with id: " + id));
    }

    @Override
    public RestaurantDTO getRestaurantDTOById(int id) {
        return new RestaurantDTO(getRestaurantById(id));
    }

    @Override
    public List<RestaurantDTO> getRestaurantsByCityId(int cityId) {
        return restaurantRepository.findByCityId(cityId).stream()
                .map(RestaurantDTO::new).toList();
    }

    @Override
    public List<MenuDTO> getMenuByRestaurantId(int restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(restaurant -> restaurant.getMenus().stream().map(MenuDTO::new).toList())
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
    public boolean setMenuByRestaurantId(int restaurantId, List<MenuDTO> menus) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        if (restaurant.updateMenus(menus)) {
            restaurantRepository.save(restaurant);
            return true;
        }
        return false;
    }

    @Override
    public boolean setTimeSlotsByRestaurantId(int restaurantId, List<TimeSlotDTO> timeSlots) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        if (restaurant.updateTimeSlots(timeSlots)) {
            restaurantRepository.save(restaurant);
            return true;
        }
        return false;
    }
}

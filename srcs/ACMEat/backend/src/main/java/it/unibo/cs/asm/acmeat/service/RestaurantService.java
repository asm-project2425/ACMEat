package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.RestaurantDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;
import it.unibo.cs.asm.acmeat.model.Restaurant;
import it.unibo.cs.asm.acmeat.service.abstractions.RestaurantServiceInterface;
import it.unibo.cs.asm.acmeat.service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RestaurantService implements RestaurantServiceInterface {
    private final RestaurantRepository restaurantRepository;

    public Restaurant getRestaurantById(int id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with id: " + id));
    }

    public List<RestaurantDTO> getRestaurantsByCityId(int cityId) {
        return restaurantRepository.findByCityId(cityId).stream()
                .map(RestaurantDTO::new).toList();
    }

    public List<MenuDTO> getMenuByRestaurantId(int restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(restaurant -> restaurant.getMenus().stream().map(MenuDTO::new).toList())
                .orElseGet(ArrayList::new);
    }

    public List<TimeSlotDTO> getTimeSlotsByRestaurantId(int restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(restaurant -> restaurant.getTimeSlots().stream().map(TimeSlotDTO::new).toList())
                .orElseGet(ArrayList::new);
    }
}

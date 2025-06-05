package it.unibo.cs.asm.acmeat.config;

import it.unibo.cs.asm.acmeat.model.City;
import it.unibo.cs.asm.acmeat.model.Restaurant;
import it.unibo.cs.asm.acmeat.model.Menu;
import it.unibo.cs.asm.acmeat.model.TimeSlot;
import it.unibo.cs.asm.acmeat.model.util.Coordinate;
import it.unibo.cs.asm.acmeat.service.repository.CityRepository;
import it.unibo.cs.asm.acmeat.service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

@RequiredArgsConstructor
@Component
public class DataLoader implements ApplicationRunner {
    private final CityRepository cityRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public void run(ApplicationArguments args) {
        loadData();
    }

    // Load initial data into the database only if the database is empty.
    private void loadData() {
        if (cityRepository.count() == 0) {
            City bologna = cityRepository.save(new City("Bologna"));

            Restaurant restaurant1 = new Restaurant("Ristorante Bolognese", new Coordinate(
                    44.49945152011542, 11.357824447074432),
                    bologna);
            // Menu
            restaurant1.addMenu(new Menu("Pasta", new BigDecimal("12.50")));
            restaurant1.addMenu(new Menu("Pizza", new BigDecimal("8.50")));
            // Time Slots (9-12 e 19-21 ogni 15 minuti)
            generateTimeSlots(restaurant1, LocalTime.of(12, 0), LocalTime.of(14, 0));
            generateTimeSlots(restaurant1, LocalTime.of(19, 0), LocalTime.of(21, 0));
            restaurantRepository.save(restaurant1);
        }
    }

    private void generateTimeSlots(Restaurant restaurant, LocalTime from, LocalTime to) {
        for (LocalTime time = from; time.isBefore(to); time = time.plusMinutes(15)) {
            restaurant.addTimeSlot(new TimeSlot(time, time.plusMinutes(15)));
        }
    }
}

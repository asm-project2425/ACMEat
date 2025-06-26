package it.unibo.cs.asm.acmeat.config;

import it.unibo.cs.asm.acmeat.model.*;
import it.unibo.cs.asm.acmeat.model.Coordinate;
import it.unibo.cs.asm.acmeat.repository.CityRepository;
import it.unibo.cs.asm.acmeat.repository.RestaurantRepository;
import it.unibo.cs.asm.acmeat.repository.ShippingCompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final ShippingCompanyRepository shippingCompanyRepository;

    @Value("${rest.restaurant.base-urls.1}")
    private String restaurantBaseUrl1;
    @Value("${rest.restaurant.base-urls.2}")
    private String restaurantBaseUrl2;
    @Value("${rest.shipping-company.base-urls.1}")
    private String shippingBaseUrl1;
    @Value("${rest.shipping-company.base-urls.2}")
    private String shippingBaseUrl2;

    @Override
    public void run(ApplicationArguments args) {
        loadData();
    }

    // Load initial data into the database only if the database is empty.
    private void loadData() {
        if (cityRepository.count() == 0) {
            City bologna = cityRepository.save(new City("Bologna"));

            Restaurant restaurant1 = new Restaurant("Ristorante Bolognese", restaurantBaseUrl1,
                    "Via S. Donato, 17d, 40127 Bologna BO", bologna);
            // Menu
            restaurant1.addMenu(new Menu("Pasta", new BigDecimal("12.50")));
            restaurant1.addMenu(new Menu("Pizza", new BigDecimal("8.50")));
            // Time Slots (9-12 e 19-21 ogni 15 minuti)
            generateTimeSlots(restaurant1, LocalTime.of(12, 0), LocalTime.of(14, 0));
            generateTimeSlots(restaurant1, LocalTime.of(19, 0), LocalTime.of(21, 0));
            restaurantRepository.save(restaurant1);

            Restaurant restaurant2 = new Restaurant("Pizzeria Italiana", restaurantBaseUrl2,
                    "Via Indipendenza, 1, 40121 Bologna BO", bologna);
            // Menu
            restaurant2.addMenu(new Menu("Margherita", new BigDecimal("7.00")));
            restaurant2.addMenu(new Menu("Capricciosa", new BigDecimal("9.00")));
            // Time Slots (9-12 e 19-21 ogni 15 minuti)
            generateTimeSlots(restaurant2, LocalTime.of(12, 0), LocalTime.of(14, 0));
            generateTimeSlots(restaurant2, LocalTime.of(19, 0), LocalTime.of(21, 0));
            restaurantRepository.save(restaurant2);
        }

        if (shippingCompanyRepository.count() == 0) {
            ShippingCompany shipping1 = new ShippingCompany("Shipping Company 1",
                    new Coordinate(44.502000, 11.351000), shippingBaseUrl1);
            shippingCompanyRepository.save(shipping1);
            ShippingCompany shipping2 = new ShippingCompany("Shipping Company 2", new Coordinate(
                    44.503000, 11.352000), shippingBaseUrl2);
            shippingCompanyRepository.save(shipping2);
        }
    }

    private void generateTimeSlots(Restaurant restaurant, LocalTime from, LocalTime to) {
        for (LocalTime time = from; time.isBefore(to); time = time.plusMinutes(15)) {
            restaurant.addTimeSlot(new TimeSlot(time, time.plusMinutes(15)));
        }
    }
}

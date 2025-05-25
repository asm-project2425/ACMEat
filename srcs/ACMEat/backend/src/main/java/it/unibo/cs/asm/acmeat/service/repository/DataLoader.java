package it.unibo.cs.asm.acmeat.service.repository;

import it.unibo.cs.asm.acmeat.model.City;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataLoader implements ApplicationRunner {
    private final CityRepository cityRepository;

    @Override
    public void run(ApplicationArguments args) {
        loadCities();
    }

    private void loadCities() {
        if (cityRepository.count() == 0) {
            cityRepository.save(new City("Milano"));
            cityRepository.save(new City("Roma"));
            cityRepository.save(new City("Torino"));
            cityRepository.save(new City("Bologna"));
            cityRepository.save(new City("Firenze"));
            cityRepository.save(new City("Napoli"));
            cityRepository.save(new City("Palermo"));
        }
    }
}

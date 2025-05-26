package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.CityDTO;
import it.unibo.cs.asm.acmeat.model.City;
import it.unibo.cs.asm.acmeat.service.abstractions.CityServiceInterface;
import it.unibo.cs.asm.acmeat.service.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CityService implements CityServiceInterface {
    private final CityRepository cityRepository;

    public City getCityById(int id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("City not found with id: " + id));
    }

    public List<CityDTO> getCities() {
        List<CityDTO> cities = new ArrayList<>();
        cityRepository.findAll().forEach(c -> cities.add(new CityDTO(c)));
        return cities;
    }
}

package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.CityDTO;
import it.unibo.cs.asm.acmeat.model.City;
import it.unibo.cs.asm.acmeat.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    @Override
    public City getCityById(int id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("City not found with id: " + id));
    }

    @Override
    public List<CityDTO> getCities() {
        List<CityDTO> cities = new ArrayList<>();
        cityRepository.findAll().forEach(c -> cities.add(new CityDTO(c)));
        return cities;
    }
}

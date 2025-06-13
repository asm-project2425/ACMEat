package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.CityDTO;
import it.unibo.cs.asm.acmeat.model.City;

import java.util.List;

public interface CityService {
    /**
     * Retrieves a city by its ID.
     *
     * @param id the ID of the city
     * @return the city with the specified ID
     */
    City getCityById(int id);

    /**
     * Retrieves a list of all cities.
     *
     * @return a list of CityDTO objects representing all cities
     */
    List<CityDTO> getCities();
}

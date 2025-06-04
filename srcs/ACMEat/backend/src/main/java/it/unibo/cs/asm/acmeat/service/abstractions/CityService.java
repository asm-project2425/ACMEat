package it.unibo.cs.asm.acmeat.service.abstractions;

import it.unibo.cs.asm.acmeat.dto.entities.CityDTO;

import java.util.List;

public interface CityService {
    List<CityDTO> getCities();
}

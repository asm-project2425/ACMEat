package it.unibo.cs.asm.acmeat.dto.response;

import it.unibo.cs.asm.acmeat.dto.entities.CityDTO;

import java.util.List;

public record RequestCitiesResponse(String correlationKey, List<CityDTO> cities) {
}

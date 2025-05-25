package it.unibo.cs.asm.acmeat.service.repository;

import it.unibo.cs.asm.acmeat.model.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends CrudRepository<City, Integer> {
}

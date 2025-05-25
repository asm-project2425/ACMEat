package it.unibo.cs.asm.acmeat.service.repository;

import it.unibo.cs.asm.acmeat.model.Restaurant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends CrudRepository<Restaurant, Integer> {
    @Query("SELECT r FROM Restaurant r WHERE r.city.id = ?1")
    List<Restaurant> findByCityId(int cityId);
}

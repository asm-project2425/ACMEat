package it.unibo.cs.asm.acmeat.service.repository;

import it.unibo.cs.asm.acmeat.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
}

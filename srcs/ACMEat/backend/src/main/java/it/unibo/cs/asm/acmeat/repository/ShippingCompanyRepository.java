package it.unibo.cs.asm.acmeat.repository;

import it.unibo.cs.asm.acmeat.model.ShippingCompany;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingCompanyRepository extends CrudRepository<ShippingCompany, Integer> {
}

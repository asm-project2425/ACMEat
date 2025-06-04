package it.unibo.cs.asm.acmeat.service.abstractions;

import it.unibo.cs.asm.acmeat.dto.entities.ShippingCompanyDTO;
import it.unibo.cs.asm.acmeat.model.ShippingCompany;
import it.unibo.cs.asm.acmeat.model.util.Coordinate;

import java.util.List;

public interface ShippingCompanyService {

    ShippingCompany getShippingCompanyById(int id);

    List<ShippingCompanyDTO> getShippingCompanies(Coordinate restaurantPosition);

}

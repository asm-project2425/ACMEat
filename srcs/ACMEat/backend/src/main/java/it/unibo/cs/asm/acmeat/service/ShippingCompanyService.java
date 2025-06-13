package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.ShippingCompanyDTO;
import it.unibo.cs.asm.acmeat.model.ShippingCompany;
import it.unibo.cs.asm.acmeat.model.Coordinate;

import java.util.List;

public interface ShippingCompanyService {
    /**
     * Retrieves a ShippingCompany by its ID.
     *
     * @param id the ID of the ShippingCompany
     * @return the ShippingCompany with the specified ID, or null if not found
     */
    ShippingCompany getShippingCompanyById(int id);

    /**
     * Retrieves a list of ShippingCompanyDTOs that can serve a restaurant based on its position.
     *
     * @param restaurantPosition the position of the restaurant
     * @return a list of ShippingCompanyDTOs that can serve the restaurant
     */
    List<ShippingCompanyDTO> getShippingCompanies(Coordinate restaurantPosition);
}

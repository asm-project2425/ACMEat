package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.ShippingCompany;
import lombok.Getter;

@Getter
public class ShippingCompanyDTO {
    private final int id;
    private final String name;
    private final CoordinateDTO position;

    public ShippingCompanyDTO(ShippingCompany shippingCompany) {
        this.id = shippingCompany.getId();
        this.name = shippingCompany.getName();
        this.position = new CoordinateDTO(shippingCompany.getPosition());
    }
}

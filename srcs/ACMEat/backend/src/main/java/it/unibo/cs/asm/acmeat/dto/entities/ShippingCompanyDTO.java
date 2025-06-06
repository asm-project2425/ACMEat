package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.ShippingCompany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ShippingCompanyDTO {
    private int id;
    private String name;
    private CoordinateDTO position;
    private String baseUrl;

    public ShippingCompanyDTO(ShippingCompany shippingCompany) {
        this.id = shippingCompany.getId();
        this.name = shippingCompany.getName();
        this.position = new CoordinateDTO(shippingCompany.getPosition());
        this.baseUrl = shippingCompany.getBaseUrl();
    }
}

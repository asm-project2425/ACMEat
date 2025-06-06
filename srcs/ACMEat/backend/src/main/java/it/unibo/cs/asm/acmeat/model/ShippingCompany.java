package it.unibo.cs.asm.acmeat.model;

import it.unibo.cs.asm.acmeat.model.util.Coordinate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "shipping_companies")
public class ShippingCompany {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    @Embedded
    private Coordinate position;
    private String baseUrl;

    public ShippingCompany(String name, Coordinate position, String baseUrl) {
        this.name = name;
        this.position = position;
        this.baseUrl = baseUrl;
    }
}

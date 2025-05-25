package it.unibo.cs.asm.acmeat.model;

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
  @ManyToOne
  private City city;
}

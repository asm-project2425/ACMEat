package it.unibo.cs.asm.acmeat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "menus")
public class Menu {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    public Menu(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }
}

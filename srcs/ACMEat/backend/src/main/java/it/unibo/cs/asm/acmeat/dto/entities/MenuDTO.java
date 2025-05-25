package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.Menu;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MenuDTO {
    private final int id;
    private final String name;
    private final BigDecimal price;

    public MenuDTO(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
    }
}

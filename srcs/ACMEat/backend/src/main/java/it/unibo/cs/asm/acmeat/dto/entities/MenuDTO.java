package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.Menu;
import lombok.Getter;

@Getter
public class MenuDTO {
    private final int id;
    private final String name;
    private final String price;

    public MenuDTO(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = String.valueOf(menu.getPrice());
    }

    public MenuDTO(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = String.valueOf(price);
    }
}

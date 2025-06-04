package it.unibo.cs.asm.acmeat.model;

import it.unibo.cs.asm.acmeat.dto.entities.MenuDTO;
import it.unibo.cs.asm.acmeat.dto.entities.TimeSlotDTO;
import it.unibo.cs.asm.acmeat.model.util.Coordinate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    @Embedded
    private Coordinate position;
    @ManyToOne
    private City city;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Menu> menus;
    @Setter
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TimeSlot> timeSlots;

    public Restaurant(String name, Coordinate position, City city) {
        this.name = name;
        this.position = position;
        this.city = city;
        this.menus = new HashSet<>();
        this.timeSlots = new HashSet<>();
    }

    public void addMenu(Menu menu) {
        menu.setRestaurant(this);
         this.menus.add(menu);
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlot.setRestaurant(this);
        this.timeSlots.add(timeSlot);
    }

    public boolean updateMenus(List<MenuDTO> menus) {
        // TODO: Implement logic to update menus
        return true;
    }

    public boolean updateTimeSlots(List<TimeSlotDTO> timeSlots) {
        // TODO: Implement logic to update time slots
        return true;
    }


}

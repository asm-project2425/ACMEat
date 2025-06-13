package it.unibo.cs.asm.acmeat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
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
    private String baseUrl;
    @Embedded
    private Coordinate position;
    @ManyToOne
    private City city;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Menu> menus;
    @Setter
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TimeSlot> timeSlots;

    public Restaurant(String name, String baseUrl, Coordinate position, City city) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.position = position;
        this.city = city;
        this.menus = new HashSet<>();
        this.timeSlots = new HashSet<>();
    }

    public boolean addMenu(Menu menu) {
        for (Menu existingMenu : this.menus) {
            if (existingMenu.getName().equalsIgnoreCase(menu.getName())) {
                return false; // Menu with the same name already exists
            }
        }
        menu.setRestaurant(this);
        return this.menus.add(menu);
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlot.setRestaurant(this);
        this.timeSlots.add(timeSlot);
    }

    public boolean updateMenu(int menuId, String name, double price) {
        for (Menu existingMenu : this.menus) {
            if (existingMenu.getId() == menuId) {
                existingMenu.setName(name);
                existingMenu.setPrice(BigDecimal.valueOf(price));
                return true;
            }
        }
        return false;
    }

    public boolean removeMenu(int menuId) {
        return this.menus.removeIf(menu -> menu.getId() == menuId);
    }

    public boolean updateTimeSlot(int timeSlotId, boolean active) {
        for (TimeSlot timeSlot : this.timeSlots) {
            if (timeSlot.getId() == timeSlotId) {
                timeSlot.setActive(active);
                return true;
            }
        }
        return false;
    }
}

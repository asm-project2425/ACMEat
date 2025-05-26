package it.unibo.cs.asm.acmeat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String address;
    @ManyToOne
    private City city;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Menu> menus;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TimeSlot> timeSlots;

    public Restaurant(String name, String address, City city) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.menus = new HashSet<>();
        this.timeSlots = new HashSet<>();
    }

    public void addMenu(Menu menu) {
        menu.setRestaurant(this);
         this.menus.add(menu);
    }

    public void removeMenu(Menu menu) {
        this.menus.remove(menu);
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlot.setRestaurant(this);
        this.timeSlots.add(timeSlot);
    }

    public void removeTimeSlot(TimeSlot timeSlot) {
        this.timeSlots.remove(timeSlot);
    }
}

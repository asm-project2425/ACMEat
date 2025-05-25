package it.unibo.cs.asm.acmeat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
    @OneToMany
    private List<Menu> menus;
    @OneToMany
    private List<TimeSlot> timeSlots;

    public Restaurant(String name, String address, City city, List<Menu> menus, List<TimeSlot> timeSlots) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.menus = menus;
        this.timeSlots = timeSlots;
    }
}

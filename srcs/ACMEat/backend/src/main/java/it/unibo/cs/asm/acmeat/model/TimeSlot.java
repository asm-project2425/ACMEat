package it.unibo.cs.asm.acmeat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "time_slots")
public class TimeSlot {
    @Id
    @GeneratedValue
    private int id;
    private LocalTime startTime;
    private LocalTime endTime;
    @Setter
    private boolean active;
    @Setter
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public TimeSlot(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.active = true;
    }
}

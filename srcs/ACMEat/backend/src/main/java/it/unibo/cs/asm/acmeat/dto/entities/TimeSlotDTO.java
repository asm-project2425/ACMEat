package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.TimeSlot;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class TimeSlotDTO {
    private final int id;
    private final String startTime;
    private final String endTime;
    private final boolean active;

    public TimeSlotDTO(TimeSlot timeSlot) {
        this.id = timeSlot.getId();
        this.startTime = timeSlot.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.endTime = timeSlot.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.active = timeSlot.isActive();
    }
}

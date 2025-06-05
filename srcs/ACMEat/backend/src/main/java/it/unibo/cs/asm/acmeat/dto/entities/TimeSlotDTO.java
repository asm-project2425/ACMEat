package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.TimeSlot;
import lombok.Getter;

@Getter
public class TimeSlotDTO {
    private final int id;
    private final String startTime;
    private final String endTime;
    private final boolean active;

    public TimeSlotDTO(TimeSlot timeSlot) {
        this.id = timeSlot.getId();
        this.startTime = String.valueOf(timeSlot.getStartTime());
        this.endTime = String.valueOf(timeSlot.getEndTime());
        this.active = timeSlot.isActive();
    }
}

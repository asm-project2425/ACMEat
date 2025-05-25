package it.unibo.cs.asm.acmeat.dto.entities;

import it.unibo.cs.asm.acmeat.model.TimeSlot;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class TimeSlotDTO {
    private final int id;
    private final OffsetDateTime startTime;
    private final OffsetDateTime endTime;

    public TimeSlotDTO(TimeSlot timeSlot) {
        this.id = timeSlot.getId();
        this.startTime = timeSlot.getStartTime();
        this.endTime = timeSlot.getEndTime();
    }
}

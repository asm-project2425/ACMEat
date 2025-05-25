package it.unibo.cs.asm.acmeat.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CreateOrderRequest {
    private final int restaurantId;
    private final int menuId;
    private final int timeSlotId;
    private final String address;

}

package it.unibo.cs.asm.acmeat.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReceiveShippingCostRequest(
        @NotBlank String correlationKey,
        @Min(0) double shippingCost
) {
}
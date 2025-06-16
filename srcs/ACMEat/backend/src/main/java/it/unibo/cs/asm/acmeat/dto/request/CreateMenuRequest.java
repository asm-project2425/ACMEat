package it.unibo.cs.asm.acmeat.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateMenuRequest(
        @NotBlank String name,
        @Min(0) double price
) {
}

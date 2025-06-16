package it.unibo.cs.asm.acmeat.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderedItemRequest(
        @NotNull int menuId,
        @Min(1) int quantity) {
}

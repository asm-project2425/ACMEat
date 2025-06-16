package it.unibo.cs.asm.acmeat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyPaymentRequest(
        @NotNull int orderId,
        @NotBlank String paymentToken) {
}

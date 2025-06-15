package it.unibo.cs.asm.acmeat.dto.request;

public record VerifyPaymentRequest(int orderId, String paymentToken) {
}

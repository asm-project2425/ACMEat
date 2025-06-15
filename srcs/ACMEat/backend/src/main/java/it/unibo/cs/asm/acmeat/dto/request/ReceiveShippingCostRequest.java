package it.unibo.cs.asm.acmeat.dto.request;

public record ReceiveShippingCostRequest(String correlationKey, double shippingCost) {
}
package it.unibo.cs.asm.acmeat.model;

public enum PaymentStatus {
    REQUESTED,
    TOKEN_VERIFICATION,
    TOKEN_VALID,
    TOKEN_INVALID,
    COMPLETED,
    REFUNDED
}

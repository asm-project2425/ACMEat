package it.unibo.cs.asm.acmeat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    private Order order;
    private PaymentStatus status;
    private String token;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

}

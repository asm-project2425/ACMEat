package it.unibo.cs.asm.acmeat.camunda.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.service.abstractions.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static it.unibo.cs.asm.acmeat.camunda.utility.ProcessConstants.*;

@RequiredArgsConstructor
@Component
public class BankWorkers {
    private final OrderService orderService;

    @JobWorker(type = JOB_PAYMENT_REQUEST)
    public Map<String, Integer> paymentRequest(@Variable int orderId, @Variable double amount) {
        int paymentId = 1244553; // Simulazione: generazione di un ID di pagamento
        return Map.of(VAR_PAYMENT_ID, paymentId);
    }

    @JobWorker(type = JOB_REQUEST_PAYMENT_REFUND)
    public void requestPaymentRefund(@Variable int orderId, @Variable int paymentId) {
        // TODO: chiamare l'API per richiedere il rimborso del pagamento
    }

    @JobWorker(type = JOB_ORDER_COMPLETED)
    public void orderCompleted(@Variable int orderId) {
        // TODO: chiamare l'API per confermare il pagamento e completare l'ordine
        orderService.orderDelivered(orderId);
    }
}

package it.unibo.cs.asm.acmeat.camunda.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.camunda.utility.ZeebeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static it.unibo.cs.asm.acmeat.camunda.utility.ProcessConstants.*;

@RequiredArgsConstructor
@Component
public class RestaurantWorkers {
    private final ZeebeService zeebeService;

    @JobWorker(type = JOB_CHECK_RESTAURANT_AVAILABILITY)
    public void checkRestaurantAvailability(@Variable String correlationKey) {
        // TODO: chiamare l'api per verificare la disponibilità del ristorante
        boolean isAvailable = true; // Simulazione di disponibilità

        zeebeService.sendMessage(MSG_RESTAURANT_AVAILABILITY, correlationKey, Map.of(
                VAR_RESTAURANT_AVAILABILITY, isAvailable));
    }
}

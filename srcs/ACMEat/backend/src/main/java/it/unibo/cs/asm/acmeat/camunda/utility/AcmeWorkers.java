package it.unibo.cs.asm.acmeat.camunda.utility;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static it.unibo.cs.asm.acmeat.camunda.utility.ProcessConstants.JOB_RETRIEVE_SHIPPING_SERVICES;

@RequiredArgsConstructor
@Component
public class AcmeWorkers {
    private final ZeebeService zeebeService;

    @JobWorker(type = JOB_RETRIEVE_SHIPPING_SERVICES)
    public void retrieveShippingServices() {
        // TODO: Implement the logic to retrieve shipping services
    }
}

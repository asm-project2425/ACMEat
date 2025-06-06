package it.unibo.cs.asm.acmeat.camunda.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.camunda.utility.ZeebeService;
import it.unibo.cs.asm.acmeat.dto.entities.ShippingCompanyDTO;
import it.unibo.cs.asm.acmeat.model.util.Coordinate;
import it.unibo.cs.asm.acmeat.service.abstractions.OrderService;
import it.unibo.cs.asm.acmeat.service.abstractions.ShippingCompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static it.unibo.cs.asm.acmeat.camunda.utility.ProcessConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class AcmeWorkers {
    private final ZeebeService zeebeService;
    private final OrderService orderService;
    private final ShippingCompanyService shippingCompanyService;

    @JobWorker(type = JOB_CANCEL_ORDER1)
    public void orderCancellation1(@Variable int orderId) {
        orderService.cancelOrder(orderId);
        log.info("Order cancellation completed for order {}", orderId);
    }

    @JobWorker(type = JOB_RETRIEVE_SHIPPING_SERVICES)
    public Map<String, List<ShippingCompanyDTO>> retrieveShippingCompanies(@Variable Coordinate restaurantPosition) {
        List<ShippingCompanyDTO> shippingCompanies = shippingCompanyService.getShippingCompanies(restaurantPosition);

        if (shippingCompanies.isEmpty()) {
            log.warn("No shipping companies found for restaurant position {}", restaurantPosition);
        }

        return Map.of(VAR_SHIPPING_COMPANIES, shippingCompanies);
    }

    @JobWorker(type = JOB_LOWEST_SHIPPING_SERVICE)
    public Map<String, Double> selectLowestShippingService(@Variable int orderId,
                                                           @Variable List<Map<String, Object>> availableCompanies) {
        Map<String, Object> selected = availableCompanies.stream()
                .min(Comparator.comparingDouble(c -> (double) c.get("shippingCost")))
                .orElseThrow(() -> new RuntimeException("No shipping companies available"));

        int shippingCompanyId = Integer.parseInt(selected.get("id").toString());
        double shippingCost = (double) selected.get("shippingCost");

        orderService.setShippingCompany(orderId, shippingCompanyService.getShippingCompanyById(shippingCompanyId));

        // TODO: notificare tutti i servizi che non sono stati selezionati (da fare in un job separato in shipping
        //  company workers)

        return Map.of(VAR_SHIPPING_COST, shippingCost);
    }

    @JobWorker(type = JOB_CANCEL_ORDER2)
    public void orderCancellation2(@Variable int orderId) {
        // TODO: chiamare l'api per cancellare l'ordine nel ristorante
        // TODO: chiamare anche l'api per cancellare l'ordine nel servizio di spedizione
        orderService.cancelOrder(orderId);
        log.info("Order cancellation completed for order {}", orderId);
    }

    @JobWorker(type = JOB_ORDER_ACTIVE)
    public void orderActive(@Variable int orderId) {
        // TODO: chiamare l'api che conferma alla shipping company che l'ordine è attivo
        orderService.orderActivated(orderId);
    }

}

package it.unibo.cs.asm.acmeat.camunda.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.dto.entities.ShippingCompanyDTO;
import it.unibo.cs.asm.acmeat.model.util.Coordinate;
import it.unibo.cs.asm.acmeat.service.abstractions.OrderService;
import it.unibo.cs.asm.acmeat.service.abstractions.ShippingCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.unibo.cs.asm.acmeat.camunda.utility.ProcessConstants.*;

@RequiredArgsConstructor
@Component
public class ShippingCompanyWorkers {
    private final ShippingCompanyService shippingCompanyService;
    private final OrderService orderService;

    @JobWorker(type = JOB_RETRIEVE_SHIPPING_SERVICES)
    public Map<String, List<ShippingCompanyDTO>> retrieveShippingCompanies(@Variable double restaurantLatitude,
                                                                           @Variable double restaurantLongitude) {
        List<ShippingCompanyDTO> shippingCompanies = shippingCompanyService.getShippingCompanies(new Coordinate(
                restaurantLatitude, restaurantLongitude));
        return Map.of(VAR_SHIPPING_COMPANIES, shippingCompanies);
    }

    @JobWorker(type = JOB_CANCEL_ORDER1)
    public void orderCancellation1(@Variable int orderId) {
        // TODO: chiamare l'api per cancellare l'ordine nel ristorante
        orderService.cancelOrder(orderId);
    }

    @JobWorker(type = JOB_SHIPPING_SERVICE_AVAILABILITY_REQUEST)
    public Map<String, String> shippingCompanyAvailabilityRequest() {
        String correlationKey = UUID.randomUUID().toString();
        // TODO: chiamare l'api passandogli l'id della richiesta e le informazioni sull'ordine
        return Map.of(VAR_SHIPPING_COMPANY_AVAILABILITY_REQUEST_ID, correlationKey);
    }

    @JobWorker(type = JOB_LOWEST_SHIPPING_SERVICE)
    public Map<String, Double> selectLowestShippingService(@Variable int orderId,
                                                           @Variable List<ShippingCompanyDTO> shippingCompanies) {
//        ShippingCompanyDTO lowestCostCompany = shippingCompanies.stream()
//                .min((c1, c2) -> Integer.compare(c1.getCost(), c2.getCost()))
//                .orElseThrow(() -> new RuntimeException("No shipping companies available"));
        int shippingCompanyId = shippingCompanies.getFirst().getId(); // Simulazione: prendo il primo della lista
        // Salvarsi la compagnia scelta nell'ordine
        orderService.setShippingCompany(orderId, shippingCompanyService.getShippingCompanyById(shippingCompanyId));

        return Map.of(VAR_SHIPPING_COST, 0.0);
    }

    @JobWorker(type = JOB_CANCEL_ORDER2)
    public void orderCancellation2(@Variable int orderId) {
        // TODO: chiamare l'api per cancellare l'ordine nel ristorante
        // TODO: chiamare anche l'api per cancellare l'ordine nel servizio di spedizione
        orderService.cancelOrder(orderId);
    }

    @JobWorker(type = JOB_ORDER_ACTIVE)
    public void orderActive(@Variable int orderId) {
        // TODO: chiamare l'api che conferma alla shipping company che l'ordine è attivo
        orderService.orderActivated(orderId);
    }

    @JobWorker(type = JOB_REQUEST_SHIPPING_CANCELLATION)
    public void requestShippingCancellation(@Variable int orderId, @Variable String shippingCompanyId) {
        // TODO: chiamare l'api per richiedere la cancellazione della spedizione
    }
}

package it.unibo.cs.asm.acmeat.process.orderManagement.api;

import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.dto.request.CreateOrderRequest;
import it.unibo.cs.asm.acmeat.dto.request.ReceiveShippingCostRequest;
import it.unibo.cs.asm.acmeat.dto.response.*;
import it.unibo.cs.asm.acmeat.process.orderManagement.worker.AcmeOMWorkers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("api/v1")
@RestController
public class OrderManagementController {
    private final AcmeOMWorkers acmeOMWorkers;

    @GetMapping("/cities")
    public ResponseEntity<RequestCitiesResponse> retrieveCities() {
        return ResponseEntity.ok(acmeOMWorkers.retrieveCitiesManually());
    }

    @GetMapping("/restaurants")
    public ResponseEntity<RequestRestaurantsResponse> retrieveRestaurants(@RequestParam String correlationKey,
                                                                          @RequestParam int cityId) {
        return ResponseEntity.ok(acmeOMWorkers.retrieveRestaurantsManually(correlationKey, cityId));
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RequestRestaurantDetailsResponse> retrieveRestaurantDetails(
            @RequestParam String correlationKey,
            @PathVariable int restaurantId) {
        return ResponseEntity.ok(acmeOMWorkers.retrieveRestaurantDetailsManually(correlationKey,
                restaurantId));
    }

    @PostMapping("/orders")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestParam String correlationKey,
                                                           @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(acmeOMWorkers.createOrderManually(correlationKey, request));
    }

    @PostMapping("/shipping-company/cost")
    public ResponseEntity<Void> receiveShippingCost(@RequestBody ReceiveShippingCostRequest request) {
        acmeOMWorkers.receiveShippingCostManually(request);
        return ResponseEntity.noContent().build();
    }

    private record ShippingCompanyInfo(String id, double shippingCost) {}

    @GetMapping("/bank/payment")
    public ResponseEntity<PaymentRedirectResponse> paymentRedirect(@RequestParam String correlationKey,
                                                                   @Variable Integer paymentId) {
        return ResponseEntity.ok(acmeOMWorkers.paymentRedirectManually(correlationKey, paymentId));
    }

    @PostMapping("/bank/verify-payment")
    public ResponseEntity<Void> verifyPayment(@RequestParam String correlationKey,
                                              @RequestParam String paymentToken) {
        acmeOMWorkers.verifyPaymentManually(correlationKey, paymentToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/orders/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestParam String correlationKey) {
        acmeOMWorkers.cancelOrderManually(correlationKey);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/orders/delivered")
    public ResponseEntity<Void> orderDelivered(@RequestParam String correlationKey) {
        acmeOMWorkers.orderDeliveredManually(correlationKey);
        return ResponseEntity.noContent().build();
    }
}

package it.unibo.cs.asm.acmeat.process.orderManagement.api;

import it.unibo.cs.asm.acmeat.dto.request.CreateOrderRequest;
import it.unibo.cs.asm.acmeat.dto.request.ReceiveShippingCostRequest;
import it.unibo.cs.asm.acmeat.dto.request.VerifyPaymentRequest;
import it.unibo.cs.asm.acmeat.dto.response.*;
import it.unibo.cs.asm.acmeat.process.orderManagement.worker.AcmeOMWorkers;
import jakarta.validation.Valid;
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
                                                           @RequestBody @Valid CreateOrderRequest request) {
        return ResponseEntity.ok(acmeOMWorkers.createOrderManually(correlationKey, request));
    }

    @PostMapping("/shipping-company/cost")
    public ResponseEntity<Void> receiveShippingCost(@RequestBody @Valid ReceiveShippingCostRequest request) {
        acmeOMWorkers.receiveShippingCostManually(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bank/payment")
    public ResponseEntity<PaymentRedirectResponse> paymentRedirect(@RequestParam int orderId) {
        return ResponseEntity.ok(acmeOMWorkers.paymentRedirectManually(orderId));
    }

    @PostMapping("/bank/verify-payment")
    public ResponseEntity<Void> verifyPayment(@RequestBody @Valid VerifyPaymentRequest request) {
        acmeOMWorkers.verifyPaymentManually(request.orderId(), request.paymentToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/orders/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestParam int orderId) {
        acmeOMWorkers.cancelOrderManually(orderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/orders/delivered")
    public ResponseEntity<Void> orderDelivered(@RequestParam int orderId) {
        acmeOMWorkers.orderDeliveredManually(orderId);
        return ResponseEntity.noContent().build();
    }
}

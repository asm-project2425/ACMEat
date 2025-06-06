package it.unibo.cs.asm.acmeat.camunda.api;

import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.camunda.utility.ZeebeService;
import it.unibo.cs.asm.acmeat.dto.entities.*;
import it.unibo.cs.asm.acmeat.dto.request.CreateOrderRequest;
import it.unibo.cs.asm.acmeat.dto.request.ReceiveShippingCostRequest;
import it.unibo.cs.asm.acmeat.dto.response.CreateOrderResponse;
import it.unibo.cs.asm.acmeat.dto.response.RequestCitiesResponse;
import it.unibo.cs.asm.acmeat.dto.response.RequestRestaurantDetailsResponse;
import it.unibo.cs.asm.acmeat.dto.response.RequestRestaurantsResponse;
import it.unibo.cs.asm.acmeat.exception.JobCompletionException;
import it.unibo.cs.asm.acmeat.model.Restaurant;
import it.unibo.cs.asm.acmeat.service.abstractions.CityService;
import it.unibo.cs.asm.acmeat.service.abstractions.OrderService;
import it.unibo.cs.asm.acmeat.service.abstractions.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.unibo.cs.asm.acmeat.camunda.utility.ProcessConstants.*;

@RequiredArgsConstructor
@RequestMapping("api/v1")
@RestController
public class OrderManagementController {
    private final ZeebeService zeebeService;
    private final CityService cityService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;

    @GetMapping("/cities")
    public ResponseEntity<RequestCitiesResponse> retrieveCities() {
        String correlationKey = UUID.randomUUID().toString();
        zeebeService.sendMessage(MSG_CITIES_REQUEST, correlationKey, Map.of(VAR_CORRELATION_KEY, correlationKey));
        List<CityDTO> cities = cityService.getCities();
        zeebeService.completeJob(JOB_RETRIEVE_CITIES, correlationKey, Map.of());

        return ResponseEntity.ok(new RequestCitiesResponse(correlationKey, cities));
    }

    @GetMapping("/restaurants")
    public ResponseEntity<RequestRestaurantsResponse> retrieveRestaurants(@RequestParam String correlationKey,
                                                                          @RequestParam int cityId) {
        zeebeService.sendMessage(MSG_CITY_SELECTED, correlationKey, Map.of());
        List<RestaurantDTO> restaurants = restaurantService.getRestaurantsByCityId(cityId);
        zeebeService.completeJob(JOB_RETRIEVE_RESTAURANTS, correlationKey, Map.of());

        return ResponseEntity.ok(new RequestRestaurantsResponse(restaurants));
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RequestRestaurantDetailsResponse> retrieveRestaurantDetails(@RequestParam String correlationKey,
                                                                                      @PathVariable int restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        zeebeService.sendMessage(MSG_RESTAURANT_SELECTED, correlationKey, Map.of(VAR_RESTAURANT_BASE_URL,
                restaurant.getBaseUrl(), VAR_RESTAURANT_POSITION, restaurant.getPosition()));
        List<MenuDTO> menus = restaurantService.getMenuByRestaurantId(restaurantId);
        List<TimeSlotDTO> timeSlots = restaurantService.getActiveTimeSlotsByRestaurantId(restaurantId);
        zeebeService.completeJob(JOB_RETRIEVE_RESTAURANT_DETAILS, correlationKey, Map.of());

        return ResponseEntity.ok(new RequestRestaurantDetailsResponse(menus, timeSlots));
    }

    @PostMapping("/orders")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestParam String correlationKey,
                                                           @RequestBody CreateOrderRequest request) {
        zeebeService.sendMessage(MSG_ORDER_CONFIRMATION, correlationKey, Map.of());
        OrderDTO order = orderService.createOrder(request.restaurantId(), request.items(), request.timeSlotId(),
                request.deliveryAddress());
        zeebeService.completeJob(JOB_CREATE_ORDER, correlationKey, Map.of(VAR_ORDER_ID, order.getId(),
                VAR_DELIVERY_TIME, order.getDeliveryTime(), VAR_DELIVERY_ADDRESS, order.getDeliveryAddress()));

        return ResponseEntity.ok(new CreateOrderResponse(order));
    }

    @PostMapping("/shipping-company/cost")
    public ResponseEntity<Void> receiveShippingCost(@RequestBody ReceiveShippingCostRequest request) {
        ShippingCompanyInfo shippingInfo = new ShippingCompanyInfo(request.correlationKey(), request.shippingCost());
        zeebeService.sendMessage(MSG_SEND_SHIPPING_COST, request.correlationKey(), Map.of(VAR_SHIPPING_INFO,
                shippingInfo));
        return ResponseEntity.noContent().build();
    }

    private record ShippingCompanyInfo(String id, double shippingCost) {}

    // ----------------------------------------------------------------------------------------------------------------

    @GetMapping("/bank/payment")
    public ResponseEntity<?> paymentRedirect(@Variable int paymentId, @RequestParam String orderId) {
        // TODO: vedere bene questo metodo
        zeebeService.sendMessage("BankRedirect", orderId, Map.of());
        zeebeService.completeJob(JOB_BANK_REDIRECT, orderId, Map.of());

        // TODO: chiamare la banca che inizializza il pagamento
        String url = "https://bank.example.com/payment?orderId=" + paymentId;

        return ResponseEntity.ok(Map.of("redirect-url", url));
    }

    @PostMapping("bank/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestParam String paymentToken) {
        // TODO: chiamare l'enpoint soap della banca per verificare il pagamento
        zeebeService.sendMessage(MSG_RECEIVE_TOKEN_TO_VERIFY, paymentToken, Map.of());
        throw new JobCompletionException(JOB_RETRIEVE_RESTAURANTS);
    }

    @PostMapping("/orders/cancel")
    public ResponseEntity<?> cancelOrder(@RequestParam String correlationKey, @RequestParam int orderId) {
        zeebeService.sendMessage(MSG_REQUEST_ORDER_CANCELLATION, correlationKey, Map.of());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/delivered")
    public ResponseEntity<?> orderDelivered(@RequestParam String correlationKey, @RequestParam int orderId) {
        zeebeService.sendMessage(MSG_ORDER_DELIVERED, correlationKey, Map.of());
        return ResponseEntity.ok().build();
    }
}

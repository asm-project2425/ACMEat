package it.unibo.cs.asm.acmeat.api;

import io.camunda.zeebe.client.ZeebeClient;
import it.unibo.cs.asm.acmeat.api.utility.ZeebeUtility;
import it.unibo.cs.asm.acmeat.dto.entities.*;
import it.unibo.cs.asm.acmeat.dto.request.CreateOrderRequest;
import it.unibo.cs.asm.acmeat.dto.response.CreateOrderResponse;
import it.unibo.cs.asm.acmeat.dto.response.RequestCitiesResponse;
import it.unibo.cs.asm.acmeat.dto.response.RequestRestaurantDetails;
import it.unibo.cs.asm.acmeat.dto.response.RequestRestaurantsResponse;
import it.unibo.cs.asm.acmeat.service.abstractions.CityServiceInterface;
import it.unibo.cs.asm.acmeat.service.abstractions.OrderServiceInterface;
import it.unibo.cs.asm.acmeat.service.abstractions.RestaurantServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.unibo.cs.asm.acmeat.api.utility.ProcessConstants.*;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class OrderManagementController extends ZeebeUtility {
    private final CityServiceInterface cityService;
    private final RestaurantServiceInterface restaurantService;
    private final OrderServiceInterface orderService;

    private String correlationKey;

    public OrderManagementController(ZeebeClient zeebeClient, CityServiceInterface cityService,
                                     RestaurantServiceInterface restaurantService, OrderServiceInterface orderService) {
        super(zeebeClient);
        this.cityService = cityService;
        this.restaurantService = restaurantService;
        this.orderService = orderService;
    }

    @GetMapping("/cities")
    public ResponseEntity<RequestCitiesResponse> retrieveCities() {
        correlationKey = UUID.randomUUID().toString();
        sendMessage(MSG_CITIES_REQUEST, correlationKey, Map.of(CORRELATION_KEY, correlationKey));
        if (!completeJob(JOB_RETRIEVE_CITIES, correlationKey, Map.of())) {
            return ResponseEntity.notFound().build();
        }
        List<CityDTO> cities = cityService.getCities();

        return ResponseEntity.ok(new RequestCitiesResponse(cities));
    }

    @GetMapping("/restaurants")
    public ResponseEntity<RequestRestaurantsResponse> retrieveRestaurants(@RequestParam int cityId) {
        sendMessage(MSG_CITY_SELECTED, correlationKey, Map.of());
        if (!completeJob(JOB_RETRIEVE_RESTAURANTS, correlationKey, Map.of())) {
            return ResponseEntity.notFound().build();
        }
        List<RestaurantDTO> restaurants = restaurantService.getRestaurantsByCityId(cityId);

        return ResponseEntity.ok(new RequestRestaurantsResponse(restaurants));
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RequestRestaurantDetails> retrieveRestaurantDetails(@PathVariable int restaurantId) {
        sendMessage(MSG_RESTAURANT_SELECTED, correlationKey, Map.of());
        if (!completeJob(JOB_RETRIEVE_RESTAURANT_DETAILS, correlationKey, Map.of())) {
            return ResponseEntity.notFound().build();
        }
        List<MenuDTO> menus = restaurantService.getMenuByRestaurantId(restaurantId);
        List<TimeSlotDTO> timeSlots = restaurantService.getTimeSlotsByRestaurantId(restaurantId);

        return ResponseEntity.ok(new RequestRestaurantDetails(menus, timeSlots));
    }

    @PostMapping("/orders")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        sendMessage(MSG_ORDER_CONFIRMATION, correlationKey, Map.of());
        if (!completeJob(JOB_CREATE_ORDER, correlationKey, Map.of())) {
            return ResponseEntity.notFound().build();
        }
        OrderDTO order = orderService.createOrder(request.restaurantId(), request.items(), request.timeSlotId(),
                request.deliveryAddress());

        return ResponseEntity.ok(new CreateOrderResponse(order));
    }
}

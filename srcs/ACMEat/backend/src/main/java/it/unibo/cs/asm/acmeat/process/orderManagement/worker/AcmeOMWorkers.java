package it.unibo.cs.asm.acmeat.process.orderManagement.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.dto.entities.*;
import it.unibo.cs.asm.acmeat.dto.request.CreateOrderRequest;
import it.unibo.cs.asm.acmeat.dto.request.ReceiveShippingCostRequest;
import it.unibo.cs.asm.acmeat.dto.response.*;
import it.unibo.cs.asm.acmeat.model.OrderStatus;
import it.unibo.cs.asm.acmeat.model.Coordinate;
import it.unibo.cs.asm.acmeat.model.Restaurant;
import it.unibo.cs.asm.acmeat.process.common.ZeebeService;
import it.unibo.cs.asm.acmeat.service.CityService;
import it.unibo.cs.asm.acmeat.service.OrderService;
import it.unibo.cs.asm.acmeat.service.RestaurantService;
import it.unibo.cs.asm.acmeat.service.ShippingCompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.unibo.cs.asm.acmeat.process.common.ProcessConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class AcmeOMWorkers {
    private final ZeebeService zeebeService;
    private final CityService cityService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final ShippingCompanyService shippingCompanyService;

    public RequestCitiesResponse retrieveCitiesManually() {
        String correlationKey = UUID.randomUUID().toString();
        zeebeService.sendMessage(MSG_CITIES_REQUEST, correlationKey, Map.of(VAR_CORRELATION_KEY, correlationKey));
        List<CityDTO> cities = cityService.getCities();
        zeebeService.completeJob(JOB_RETRIEVE_CITIES, correlationKey, Map.of());

        return new RequestCitiesResponse(correlationKey, cities);
    }

    public RequestRestaurantsResponse retrieveRestaurantsManually(String correlationKey, int cityId) {
        zeebeService.sendMessage(MSG_CITY_SELECTED, correlationKey, Map.of());
        List<RestaurantDTO> restaurants = restaurantService.getRestaurantsByCityId(cityId);
        zeebeService.completeJob(JOB_RETRIEVE_RESTAURANTS, correlationKey, Map.of());

        return new RequestRestaurantsResponse(restaurants);
    }

    public RequestRestaurantDetailsResponse retrieveRestaurantDetailsManually(String correlationKey, int restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        zeebeService.sendMessage(MSG_RESTAURANT_SELECTED, correlationKey, Map.of(VAR_RESTAURANT_BASE_URL,
                restaurant.getBaseUrl(), VAR_RESTAURANT_POSITION, restaurant.getPosition()));
        List<MenuDTO> menus = restaurantService.getMenuByRestaurantId(restaurantId);
        List<TimeSlotDTO> timeSlots = restaurantService.getActiveTimeSlotsByRestaurantId(restaurantId);
        zeebeService.completeJob(JOB_RETRIEVE_RESTAURANT_DETAILS, correlationKey, Map.of());

        return new RequestRestaurantDetailsResponse(menus, timeSlots);
    }

    public CreateOrderResponse createOrderManually(String correlationKey, CreateOrderRequest request) {
        zeebeService.sendMessage(MSG_ORDER_CONFIRMATION, correlationKey, Map.of());
        OrderDTO order = orderService.createOrder(request.restaurantId(), request.items(), request.timeSlotId(),
                request.deliveryAddress());
        zeebeService.completeJob(JOB_CREATE_ORDER, correlationKey, Map.of(VAR_ORDER_ID, order.getId(), VAR_ORDER_PRICE,
                order.getPrice(), VAR_DELIVERY_TIME, order.getDeliveryTime(), VAR_DELIVERY_ADDRESS,
                order.getDeliveryAddress()));

        return new CreateOrderResponse(order);
    }

    @JobWorker(type = JOB_CANCEL_ORDER)
    public void orderCancellation(@Variable int orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        log.info("Order cancellation completed for order {}", orderId);
    }

    @JobWorker(type = JOB_RETRIEVE_SHIPPING_SERVICES)
    public Map<String, List<ShippingCompanyDTO>> retrieveShippingCompanies(@Variable int orderId,
                                                                           @Variable Coordinate restaurantPosition) {
        orderService.updateOrderStatus(orderId, OrderStatus.RESTAURANT_CONFIRMED);
        List<ShippingCompanyDTO> shippingCompanies = shippingCompanyService.getShippingCompanies(restaurantPosition);

        if (shippingCompanies.isEmpty()) {
            log.warn("No shipping companies found for restaurant position {}", restaurantPosition);
        }

        return Map.of(VAR_SHIPPING_COMPANIES, shippingCompanies);
    }

    public void receiveShippingCostManually(ReceiveShippingCostRequest request) {
        ShippingCompanyInfo shippingInfo = new ShippingCompanyInfo(request.correlationKey(), request.shippingCost());
        zeebeService.sendMessage(MSG_SEND_SHIPPING_COST, request.correlationKey(), Map.of(VAR_SHIPPING_INFO,
                shippingInfo));
    }

    private record ShippingCompanyInfo(String id, double shippingCost) {}

    @JobWorker(type = JOB_LOWEST_SHIPPING_SERVICE)
    public Map<String, Double> selectLowestShippingService(@Variable int orderId,
                                                           @Variable List<Map<String, Object>> availableCompanies) {
        Map<String, Object> selected = availableCompanies.stream()
                .min(Comparator.comparingDouble(c -> (double) c.get("shippingCost")))
                .orElseThrow(() -> new RuntimeException("No shipping companies available"));

        int shippingCompanyId = Integer.parseInt(selected.get("id").toString());
        double shippingCost = (double) selected.get("shippingCost");

        orderService.setShippingCompany(orderId, shippingCompanyService.getShippingCompanyById(shippingCompanyId));
        orderService.updateOrderStatus(orderId, OrderStatus.SHIPPING_COMPANY_CHOSEN);

        return Map.of(VAR_SHIPPING_COST, shippingCost);
    }

    public PaymentRedirectResponse paymentRedirectManually(String correlationKey, Integer paymentId) {
        zeebeService.sendMessage(MSG_COMPLETE_PAYMENT, correlationKey, Map.of());
        zeebeService.completeJob(JOB_BANK_REDIRECT, correlationKey, Map.of());

        String redirectUrl = "https://bank-frontend/payment?paymentId=" + paymentId;

        return new PaymentRedirectResponse(redirectUrl);
    }

    public void verifyPaymentManually(String correlationKey, String paymentToken) {
        zeebeService.sendMessage(MSG_RECEIVE_TOKEN_TO_VERIFY, correlationKey, Map.of(VAR_PAYMENT_TOKEN, paymentToken));
    }

    @JobWorker(type = JOB_ORDER_ACTIVE)
    public void orderActive(@Variable int orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.PAID);
    }

    public void cancelOrderManually(String correlationKey) {
        zeebeService.sendMessage(MSG_REQUEST_ORDER_CANCELLATION, correlationKey, Map.of());
    }

    @JobWorker(type = JOB_CANCELLATION_REJECTED)
    public void orderCancellationRejected(@Variable int orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.CANCELLATION_REJECTED);
    }

    public void orderDeliveredManually(String correlationKey) {
        zeebeService.sendMessage(MSG_ORDER_DELIVERED, correlationKey, Map.of());
    }

    @JobWorker(type = JOB_ORDER_COMPLETED)
    public void orderCompleted(@Variable int orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.DELIVERED);
    }
}

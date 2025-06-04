package it.unibo.cs.asm.acmeat.camunda.utility;

public final class ProcessConstants {

    public static final String PROCESS_ID = "acmeat-process";

    // Process Variables
    public static final String CORRELATION_KEY = "correlationKey";
    public static final String VAR_RESTAURANT_AVAILABILITY = "restaurantAvailability";
    public static final String VAR_SHIPPING_COMPANIES = "shippingCompanies";
    public static final String VAR_SHIPPING_COMPANY_AVAILABILITY_REQUEST_ID = "shippingCompanyAvailabilityRequestId";
    public static final String VAR_SHIPPING_COST = "shippingCost";
    public static final String VAR_PAYMENT_ID = "paymentId";

    // TASKS: Order Management
    public static final String JOB_RETRIEVE_CITIES = "retrieve-cities";
    public static final String JOB_RETRIEVE_RESTAURANTS = "retrieve-restaurants";
    public static final String JOB_RETRIEVE_RESTAURANT_DETAILS = "retrieve-restaurant-details";
    public static final String JOB_CREATE_ORDER = "create-order";
    public static final String JOB_CHECK_RESTAURANT_AVAILABILITY = "check-restaurant-availability";
    public static final String JOB_CANCEL_ORDER1 = "cancel-order1";
    public static final String JOB_RETRIEVE_SHIPPING_SERVICES = "retrieve-shipping-services";
    public static final String JOB_SHIPPING_SERVICE_AVAILABILITY_REQUEST = "shipping-service-availability-request";
    public static final String JOB_LOWEST_SHIPPING_SERVICE = "select-lowest-shipping-service";
    public static final String JOB_CANCEL_ORDER2 = "cancel-order2";
    public static final String JOB_PAYMENT_REQUEST = "payment-request";
    public static final String JOB_BANK_REDIRECT = "bank-redirect";
    public static final String JOB_ORDER_ACTIVE = "order-active";
    public static final String JOB_REQUEST_PAYMENT_REFUND = "request-payment-refund";
    public static final String JOB_REQUEST_SHIPPING_CANCELLATION = "request-shipping-cancellation";
    public static final String JOB_ORDER_COMPLETED = "order-completed";

    // TASKS: Restaurant Management
    public static final String JOB_RETRIEVE_RESTAURANT_INFORMATION = "retrieve-restaurant-information";
    public static final String JOB_UPDATE_RESTAURANT_INFORMATION = "update-restaurant-information";

    // Messages: Customer → ACMEat
    public static final String MSG_CITIES_REQUEST = "CitiesRequest";
    public static final String MSG_CITY_SELECTED = "CitySelected";
    public static final String MSG_RESTAURANT_SELECTED = "RestaurantSelected";
    public static final String MSG_ORDER_CONFIRMATION = "OrderConfirmation";
    public static final String MSG_RECEIVE_TOKEN_TO_VERIFY = "ReceiveTokenToVerify";
    public static final String MSG_REQUEST_ORDER_CANCELLATION = "RequestOrderCancellation";

    // Messages: Restaurant → ACMEat
    public static final String MSG_REQUEST_RESTAURANT_INFORMATION = "RequestRestaurantInformation";
    public static final String MSG_RESTAURANT_INFORMATION_UPDATED = "RestaurantInformationUpdated";
    public static final String MSG_RESTAURANT_AVAILABILITY = "RestaurantAvailability";

    // Messages: Shipping Service → ACMEat
    public static final String MSG_SEND_SHIPPING_COST = "send-shipping-cost";
    public static final String MSG_ORDER_DELIVERED = "OrderDelivered";


    private ProcessConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

}

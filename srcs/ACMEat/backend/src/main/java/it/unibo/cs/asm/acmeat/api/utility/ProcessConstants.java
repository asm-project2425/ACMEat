package it.unibo.cs.asm.acmeat.api.utility;

public final class ProcessConstants {

    public static final String PROCESS_ID = "acmeat-process";
    public static final String CORRELATION_KEY = "correlationKey";

    // TASKS
    public static final String JOB_RETRIEVE_CITIES = "retrieve-cities";
    public static final String JOB_RETRIEVE_RESTAURANTS = "retrieve-restaurants";
    public static final String JOB_RETRIEVE_RESTAURANT_DETAILS = "retrieve-restaurant-details";
    public static final String JOB_CREATE_ORDER = "create-order";

    // Messages: Customer → ACMEat
    public static final String MSG_CITIES_REQUEST = "CitiesRequest";
    public static final String MSG_CITY_SELECTED = "CitySelected";
    public static final String MSG_RESTAURANT_SELECTED = "RestaurantSelected";
    public static final String MSG_ORDER_CONFIRMATION = "OrderConfirmation";
    public static final String MSG_RECEIVE_TOKEN_TO_VERIFY = "ReceiveTokenToVerify";
    public static final String MSG_REQUEST_ORDER_CANCELLATION = "RequestOrderCancellation";

    // Messages: Shipping Service → ACMEat
    public static final String MSG_SHIPPING_SERVICE_AVAILABLE = "ShippingServiceAvailable";
    public static final String MSG_ORDER_DELIVERED = "OrderDelivered";

    // Messages: Bank -> ACMEat
    public static final String MSG_PAYMENT_DETAILS_REQUEST= "PaymentDetailsRequest";
    public static final String MSG_VALID_PAYMENT = "ValidPayment";
    public static final String MSG_INVALID_PAYMENT = "InvalidPayment";

    private ProcessConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

}

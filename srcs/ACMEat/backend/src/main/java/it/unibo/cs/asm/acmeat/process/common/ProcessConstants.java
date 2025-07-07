package it.unibo.cs.asm.acmeat.process.common;

public final class ProcessConstants {
    // Process Variables
    public static final String VAR_CORRELATION_KEY = "correlationKey";
    public static final String VAR_IS_BETWEEN_21_AND_10 = "isBetween21And10";
    public static final String VAR_RESTAURANT_BASE_URL = "restaurantBaseUrl";
    public static final String VAR_RESTAURANT_ADDRESS = "restaurantAddress";
    public static final String VAR_ORDER_ID = "orderId";
    public static final String VAR_ORDER_PRICE = "orderPrice";
    public static final String VAR_DELIVERY_TIME = "deliveryTime";
    public static final String VAR_DELIVERY_ADDRESS = "deliveryAddress";
    public static final String VAR_RESTAURANT_AVAILABILITY = "restaurantAvailability";
    public static final String VAR_SHIPPING_COMPANIES = "shippingCompanies";
    public static final String VAR_SHIPPING_COMPANY_CORRELATION_KEY = "shippingCompanyCorrelationKey";
    public static final String VAR_SHIPPING_INFO = "shippingInfo";
    public static final String VAR_SHIPPING_COST = "shippingCost";
    public static final String VAR_SHIPPING_COMPANY_BASE_URL = "shippingCompanyBaseUrl";
    public static final String VAR_DELIVERY_ID = "deliveryId";
    public static final String VAR_PAYMENT_ID = "paymentId";
    public static final String VAR_PAYMENT_TOKEN = "paymentToken";
    public static final String VAR_VALID_PAYMENT = "validPayment";
    public static final String VAR_IS_ONE_HOUR_BEFORE = "atLeastOneHourBeforeDelivery";

    // JOBS: Order Management
    public static final String JOB_RETRIEVE_CITIES = "retrieve-cities";
    public static final String JOB_RETRIEVE_RESTAURANTS = "retrieve-restaurants";
    public static final String JOB_RETRIEVE_RESTAURANT_DETAILS = "retrieve-restaurant-details";
    public static final String JOB_CREATE_ORDER = "create-order";
    public static final String JOB_CHECK_RESTAURANT_AVAILABILITY = "check-restaurant-availability";
    public static final String JOB_CANCEL_ORDER = "cancel-order";
    public static final String JOB_CANCEL_RESTAURANT_ORDER = "cancel-restaurant-order";
    public static final String JOB_RETRIEVE_SHIPPING_SERVICES = "retrieve-shipping-services";
    public static final String JOB_SHIPPING_SERVICE_AVAILABILITY_REQUEST = "shipping-service-availability-request";
    public static final String JOB_LOWEST_SHIPPING_SERVICE = "select-lowest-shipping-service";
    public static final String JOB_PAYMENT_REQUEST = "payment-request";
    public static final String JOB_SAVE_PAYMENT = "save-payment";
    public static final String JOB_BANK_REDIRECT = "bank-redirect";
    public static final String JOB_VERIFY_PAYMENT_TOKEN = "verify-payment-token";
    public static final String JOB_ORDER_ACTIVE = "order-active";
    public static final String JOB_CONFIRM_SHIPPING_COMPANY = "confirm-shipping-company";
    public static final String JOB_CONFIRM_PAYMENT = "confirm-payment";
    public static final String JOB_CANCELLATION_REJECTED = "cancellation-rejected";
    public static final String JOB_REQUEST_PAYMENT_REFUND = "request-payment-refund";
    public static final String JOB_REQUEST_SHIPPING_CANCELLATION = "request-shipping-cancellation";
    public static final String JOB_ORDER_COMPLETED = "order-completed";

    // JOBS: Restaurant Management
    public static final String JOB_RETRIEVE_RESTAURANT_INFORMATION = "retrieve-restaurant-information";
    public static final String JOB_UPDATE_RESTAURANT_INFORMATION = "update-restaurant-information";

    // Messages: Customer → ACMEat
    public static final String MSG_CITIES_REQUEST = "CitiesRequest";
    public static final String MSG_CITY_SELECTED = "CitySelected";
    public static final String MSG_RESTAURANT_SELECTED = "RestaurantSelected";
    public static final String MSG_ORDER_CONFIRMATION = "OrderConfirmation";
    public static final String MSG_RECEIVE_TOKEN_TO_VERIFY = "ReceiveTokenToVerify";
    public static final String MSG_COMPLETE_PAYMENT = "CompletePayment";
    public static final String MSG_REQUEST_ORDER_CANCELLATION = "RequestOrderCancellation";

    // Messages: Restaurant → ACMEat
    public static final String MSG_REQUEST_RESTAURANT_INFORMATION = "RequestRestaurantInformation";
    public static final String MSG_RESTAURANT_INFORMATION_UPDATED = "RestaurantInformationUpdated";
    public static final String MSG_RESTAURANT_AVAILABILITY = "RestaurantAvailability";

    // Messages: Shipping Service → ACMEat
    public static final String MSG_SEND_SHIPPING_COST = "ShippingServiceAvailable";
    public static final String MSG_ORDER_DELIVERED = "OrderDelivered";

    // Messages: Bank → ACMEat
    public static final String MSG_PAYMENT_REQUEST = "PaymentDetailsRequest";
    public static final String MSG_PAYMENT_VALIDITY = "PaymentValidity";

    private ProcessConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

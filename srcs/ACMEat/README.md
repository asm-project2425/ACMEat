# ACMEat

## API Documentation

All APIs are exposed on `http://localhost:8080`.  
Interactive Swagger UI is available at:  **http://localhost:8080/swagger-ui/index.html**

---

## Process APIs (Camunda-managed)

These endpoints interact directly with the BPMN process engine via message correlation or job completion.

### Process: `orderManagement`

#### `retrieveCities`
- **Method**: `GET`
- **URL**: `/api/v1/cities`
- **Description**: Starts the process and sends the `requestCities` message, then completes the `Retrieve cities` task.
- **Response**: Includes a `correlationKey` and a list of available cities.

#### `retrieveRestaurants`
- **Method**: `GET`
- **URL**: `/api/v1/restaurants?correlationKey=...&cityId=...`
- **Query Parameters**:
  - `correlationKey`: Correlation key obtained from `retrieveCities`
  - `cityId`: ID of the selected city
- **Description**: Sends the `citySelected` message and completes the `Retrieve restaurants` task.
- **Response**: List of restaurants available in the specified city.

#### `retrieveRestaurantDetails`
- **Method**: `GET`
- **URL**: `/api/v1/restaurants/{restaurantId}?correlationKey=...`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
- **Query Parameters**:
  - `correlationKey`: Correlation key obtained from `retrieveCities`
- **Description**: Sends the `restaurantSelected` message and completes the `Retrieve restaurant details` task.
- **Response**: List of menus and time slots for the selected restaurant.

#### `createOrder`
- **Method**: `POST`
- **URL**: `/api/v1/orders?correlationKey=...`
- **Query Parameters**:
  - `correlationKey`: Correlation key obtained from `retrieveCities`
- **Request Body**:
```json
{
  "restaurantId": 1,
  "items": [
    { "menuId": 1, "quantity": 2 },
    { "menuId": 2, "quantity": 1 }
  ],
  "timeSlotId": 5,
  "deliveryAddress": "Via Roma 42, Bologna"
}
```
- **Description**: Sends the `orderConfirmation` message and completes the `Create order` task.
- **Response**: Returns the created order details.

#### `receiveShippingCost`
- **Method**: `POST`
- **URL**: `/api/v1/shipping-company/cost`
- **Request Body**:
```json
{
  "correlationKey": "abc-123",
  "shippingCost": 5.90
}
```
- **Description**: Receives the cost from a shipping company and sends the `sendShippingCost` message with the cost as a process variable (shippingInfo). This endpoint must be called within 15 seconds from the initial availability request.
- **Response**: HTTP `204 No Content`.

#### `paymentRedirect`
- **Method**: `GET`
- **URL**: `/api/v1/bank/payment?correlationKey=...&paymentId=...`
- **Query Parameters**:
  - `correlationKey`: Correlation key associated with the current process
  - `paymentId`: ID of the payment to complete
- **Description**: Sends the `completePayment` message and completes the `Redirect to bank` task. Returns a URL for the user to complete the payment.
- **Response**: JSON object containing the URL to redirect the user.

#### `verifyPayment`
- **Method**: `POST`
- **URL**: `/api/v1/bank/verify-payment?correlationKey=...&paymentToken=...`
- **Query Parameters**:
  - `correlationKey`: Correlation key associated with the current process
  - `paymentToken`: Token received by the user from the bank
- **Description**: Sends the `receiveTokenToVerify` message with the provided token to validate the payment.
- **Response**: HTTP `204 No Content`.

#### `cancelOrder`
- **Method**: `POST`
- **URL**: `/api/v1/orders/cancel?correlationKey=...`
- **Query Parameters**:
  - `correlationKey`: Correlation key associated with the current process
- **Description**: Sends the `requestOrderCancellation` message to cancel an active order.
- **Response**: HTTP `204 No Content`.

#### `orderDelivered`
- **Method**: `POST`
- **URL**: `/api/v1/orders/delivered?correlationKey=...`
- **Query Parameters**:
  - `correlationKey`: Correlation key associated with the current process
- **Description**: Sends the `orderDelivered` message to indicate that the order has been successfully delivered.
- **Response**: HTTP `204 No Content`.

---

### Process: `restaurantManagement`

#### `retrieveRestaurantInformation`
- **Method**: `GET`
- **URL**: `/api/v1/restaurants/{restaurantId}/information`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
- **Description**: Starts the *restaurantManagement* process, sends the `requestRestaurantInformation` message and completes the `Retrieve restaurant information` task. This operation is only allowed between **22:00 and 10:00**
- **Response**: Includes a `correlationKey`, list of `menus`, and `timeSlots`.

#### `confirmRestaurantInformationUpdate`
- **Method**: `POST`
- **URL**: `/api/v1/restaurants/confirm`
- **Query Parameters**:
  - `correlationKey`: Correlation key obtained from `retrieveRestaurantInformation`
- **Description**: Confirms and completes the restaurant information update in the BPMN engine.
- **Response**: HTTP `204 No Content`.

---

## Domain APIs

These endpoints interact directly with the ACMEat domain model and services.

### `getOrderStatus`
- **Method**: `GET`
- **URL**: `/api/v1/orders/{orderId}/status`
- **Path Parameters**:
  - `orderId`: ID of the order to check the status
- **Description**: Retrieves the current status of the specified order.
- **Response**: Order ID and status (`CREATED`, `CANCELLED`, `RESTAURANT_CONFIRMED`, `SHIPPING_COMPANY_CHOSEN`, `PAID`, `CANCELLATION_REJECTED`, `DELIVERED`).

### `getRestaurantsByCity`
- **Method**: `GET`
- **URL**: `/api/v1/restaurants?cityId=...`
- **Query Parameters**:
  - `cityId`: ID of the city
- **Description**: Returns the list of restaurants in the specified city.
- **Response**: List of `RestaurantDTO`.

### `getRestaurantById`
- **Method**: `GET`
- **URL**: `/api/v1/restaurants/{restaurantId}`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
- **Response**: Restaurant details (`RestaurantDTO`).

### `addMenu`
- **Method**: `POST`
- **URL**: `/api/v1/restaurants/{restaurantId}/menus`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
- **Request Body**:
```json
{ "name": "New dish", "price": 8.50 }
```
- **Description**: Adds a new menu item to the specified restaurant.
- **Response**: Created `MenuDTO` object.

### `updateMenu`
- **Method**: `PUT`
- **URL**: `/api/v1/restaurants/{restaurantId}/menus/{menuId}`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
  - `menuId`: ID of the menu item to update
- **Request Body**:
```json
{ "name": "Updated dish", "price": 9.00 }
```
- **Description**: Updates an existing menu item for the specified restaurant.
- **Response**: Updated `MenuDTO` object.

### `deleteMenu`
- **Method**: `DELETE`
- **URL**: `/api/v1/restaurants/{restaurantId}/menus/{menuId}`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
  - `menuId`: ID of the menu item to delete
- **Description**: Deletes the specified menu item.
- **Response**: HTTP `204 No Content`.

### `updateTimeSlot`
- **Method**: `PUT`
- **URL**: `/api/v1/restaurants/{restaurantId}/timeslots/{timeSlotId}?active=true`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
  - `timeSlotId`: ID of the time slot
- **Query Parameters**:
  - `active`: `true` to activate, `false` to deactivate
- **Description**: Updates the active state of a time slot.
- **Response**: Updated `TimeSlotDTO` object.

---
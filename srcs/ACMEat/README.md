# ACMEat

## API Documentation

All APIs are exposed on `http://localhost:8080`.  
Interactive Swagger UI is available at:  **http://localhost:8080/swagger-ui/index.html**

---

## Process: `orderManagement`

### `retrieveCities`
- **Method**: `GET`
- **URL**: `/api/v1/cities`
- **Description**: Starts the process and sends the `requestCities` message, then completes the `Retrieve cities` task.
- **Response**: Includes a `correlationKey` and a list of available cities.

---

### `retrieveRestaurants`
- **Method**: `GET`
- **URL**: `/api/v1/restaurants?correlationKey=...&cityId=...`
- **Query Parameters**:
  - `correlationKey`: Correlation key obtained from `retrieveCities`
  - `cityId`: ID of the selected city
- **Description**: Sends the `citySelected` message and completes the `Retrieve restaurants` task.
- **Response**: List of restaurants available in the specified city.

---

### `retrieveRestaurantDetails`
- **Method**: `GET`
- **URL**: `/api/v1/restaurants/{restaurantId}?correlationKey=...`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
- **Query Parameters**:
  - `correlationKey`: Correlation key obtained from `retrieveCities`
- **Description**: Sends the `restaurantSelected` message and completes the `Retrieve restaurant details` task.
- **Response**: List of menus and time slots for the selected restaurant.

---

### `createOrder`
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

---

### `receiveShippingCost`
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

---

## Process: `restaurantManagement`

### `retrieveRestaurantInformation`
- **Method**: `GET`
- **URL**: `/api/v1/restaurants/{restaurantId}/information`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
- **Description**: Starts the *restaurantManagement* process, sends the `requestRestaurantInformation` message and completes the `Retrieve restaurant information` task. This operation is only allowed between **22:00 and 10:00**
- **Response**: Includes a `correlationKey`, list of `menus`, and `timeSlots`.

---

### `confirmRestaurantInformationUpdate`
- **Method**: `POST`
- **URL**: `/api/v1/restaurants/confirm`
- **Query Parameters**:
  - `correlationKey`: Correlation key obtained from `retrieveRestaurantInformation`
- **Description**: Confirms and completes the restaurant information update in the BPMN engine.
- **Response**: HTTP `204 No Content`.

---
> **Note**:  
> The following endpoints are exposed to allow the user to interactively modify restaurant information (menus and time slots) through the frontend. These operations **do not interact directly with the Camunda process engine**. The BPMN process remains pending until the user confirms the update using the `confirmRestaurantInformationUpdate` endpoint.

### `addMenu`
- **Method**: `POST`
- **URL**: `/api/v1/restaurants/{restaurantId}/menus`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
- **Request Body**:
```json
{
  "name": "New dish",
  "price": 8.50
}
```
- **Description**: Adds a new menu item to the specified restaurant.
- **Response**: Created `MenuDTO` object.

---

### `updateMenu`
- **Method**: `PUT`
- **URL**: `/api/v1/restaurants/{restaurantId}/menus/{menuId}`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
  - `menuId`: ID of the menu item to update
- **Request Body**:
```json
{
  "name": "Updated dish",
  "price": 9.00
}
```
- **Description**: Updates an existing menu item for the specified restaurant.
- **Response**: Updated `MenuDTO` object.

---

### `deleteMenu`
- **Method**: `DELETE`
- **URL**: `/api/v1/restaurants/{restaurantId}/menus/{menuId}`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
  - `menuId`: ID of the menu item to delete
- **Description**: Deletes the specified menu item.
- **Response**: HTTP `204 No Content`.

---

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

# ACMEat

## API Documentation

All APIs are exposed on `http://localhost:8080`.  
Interactive Swagger UI is available at:  **http://localhost:8080/swagger-ui/index.html**

---

## Process: `orderManagement`

### `retrieveCities`
- **Method**: `GET`
- **URL**: `/api/v1/cities`
- **Description**: Retrieves the list of available cities.

---

### `retrieveRestaurants`
- **Method**: `GET`
- **URL**: `/api/v1/restaurants?cityId=1`
- **Query Parameters**:
  - `cityId`: ID of the city to filter restaurants.
- **Description**: Retrieves restaurants in the specified city.

---

### `retrieveRestaurantDetails`
- **Method**: `GET`
- **URL**: `/api/v1/restaurants/{restaurantId}`
- **Path Parameters**:
  - `restaurantId`: ID of the restaurant
- **Description**: Retrieves full details of the specified restaurant.

---

### `createOrder`
- **Method**: `POST`
- **URL**: `/api/v1/orders`
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
- **Description**: Creates a new order with selected items and time slot.

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
- **Description**: Completes the update process in the BPMN engine.
- **Response**: `true` if successful, `false` otherwise.

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
- **Description**: Adds a new menu item.
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
- **Description**: Updates an existing menu item.
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

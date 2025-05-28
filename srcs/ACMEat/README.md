# ACMEat

## API Documentation: acme

Swagger documentation for the ACMEat API is available at: http://localhost:8080/swagger-ui/index.html

### `retrieveCities`
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/v1/cities`

### `retrieveRestaurants`
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/v1/restaurants?cityId=1`
- **Query Parameters**:
    - `cityId`: `1`

### `retrieveRestaurantDetails`
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/v1/restaurants/1`

### `createOrder`
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/v1/orders`
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


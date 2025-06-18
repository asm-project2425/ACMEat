# Shipping Company

The public API of the shipping company is exposed by the shipping management service.

The vehicle tracker service exposes a frontend to track the company's vehicles.

## API Documentation

### Shipping Management

#### `availability`
Checks the availability of a vehicle for a delivery, reserving it if successful.
The reservation is kept for 2 minutes. If the delivery is not confirmed within this time &ndash; via
the `confirm` API &ndash; the reservation will be cancelled.
- **Method**: `POST`
- **URL**: `/api/v1/availability`
- **Body**:
```json
{
  "correlationKey": 0,
  "orderId": 5,
  "deliveryTime": "2025-06-16T16:10:00.000Z",
  "restaurantAddress": "address 1",
  "deliveryAddress": "address 2"
}
```

#### `confirm`
Confirms a vehicle reservation.
- **Method**: `POST`
- **URL**: `/api/v1/confirm`
- **Body**:
```json
{
  "deliveryId": 0
}
```

#### `cancel`
Cancels a delivery or a vehicle reservation.
- **Method**: `POST`
- **URL**: `/api/v1/cancel`
- **Body**:
```json
{
  "deliveryId": 0
}
```

### Vehicle Assigner

#### `reserve`
Reserves a vehicle for a delivery. Fails if no vehicle is available.
- **Method**: `POST`
- **URL**: `/api/v1/reserve`
- **Body**:
```json
{
  "orderId": 5,
  "deliveryTime": "2025-06-16T16:10:00.000Z",
  "cost": 1.5,
  "restaurantAddress": "address 1",
  "deliveryAddress": "address 2"
}
```
- **Response**:
```json
{
  "deliveryId": 3
}
```

#### `confirmDelivery`
Confirms a vehicle reservation for a delivery.
- **Method**: `POST`
- **URL**: `/api/v1/confirmDelivery`
- **Body**:
```json
{
  "deliveryId": 0
}
```

#### `cancelDelivery`
Cancels a delivery or a vehicle reservation.
- **Method**: `POST`
- **URL**: `/api/v1/cancelDelivery`
- **Body**:
```json
{
  "deliveryId": 0
}
```

### Vehicle Tracker

#### `deliveryStarted`
Start tracking a vehicle for a delivery.
- **Method**: `POST`
- **URL**: `/api/v1/deliveryStarted`
- **Body**:
```json
{
  "vehicleId": 2,
  "deliveryId": 0
}
```

#### `deliveryEnded`
Stop tracking a vehicle.
- **Method**: `POST`
- **URL**: `/api/v1/deliveryEnded`
- **Body**:
```json
{
  "vehicleId": 2
}
```

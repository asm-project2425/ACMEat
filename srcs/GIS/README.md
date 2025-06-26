# GIS

## API Documentation: gis

### `locate`
Returns the latitude and longitude of a location.
- **Method**: `GET`
- **URL**: `http://localhost:6002/api/v1/locate?{query}`
- **Query Parameters**:
    - `query`: city name, street, address, etc.
    - `lat`, `lon` (*optionals*): if present, returns the closest location found to the provided coordinates
- **Response**:
```json
{
  "lat": 44.4938203,
  "lon": 11.3426327
}
```

### `locateCity`
Returns the latitude and longitude of a city.
- **Method**: `GET`
- **URL**: `http://localhost:6002/api/v1/locateCity?{city}`
- **Query Parameters**:
    - `city`: city name
    - `lat`, `lon` (*optionals*): if present, returns the closest location found to the provided coordinates
- **Response**:
```json
{
  "lat": 44.4938203,
  "lon": 11.3426327
}
```

### `distance`
Returns the distance in meters between two locations.
- **Method**: `GET`
- **URL**: `http://localhost:6002/api/v1/distance?{lat1}&{lon1}&{lat2}&{lon2}`
- **Query Parameters**:
    - `lat1`: latitude of first location
    - `lon1`: longitude of first location
    - `lat2`: latitude of second location
    - `lon2`: longitude of second location
- **Response**:
```json
{
  "distance": 1632
}
```
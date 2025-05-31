# GIS

## API Documentation: gis

### `locate`
Returns the latitude and longitude of a location.
- **Method**: `GET`
- **URL**: `http://localhost:6002/api/v1/locate?{query}`
- **Query Parameters**:
    - `query`: city name, street, address

### `locateCity`
Returns the latitude and longitude of a city.
- **Method**: `GET`
- **URL**: `http://localhost:6002/api/v1/locateCity?{city}`
- **Query Parameters**:
    - `city`: city name

### `distance`
Returns the distance in meters between two locations.
- **Method**: `GET`
- **URL**: `http://localhost:6002/api/v1/distance?{lat1}&{lon1}&{lat2}&{lon2}`
- **Query Parameters**:
    - `lat1`: latitude of first location
    - `lon1`: longitude of first location
    - `lat2`: latitude of second location
    - `lon2`: longitude of second location
